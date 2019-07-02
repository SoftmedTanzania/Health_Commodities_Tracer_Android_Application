package com.softmed.stockapp.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;

import com.softmed.stockapp.Database.AppDatabase;
import com.softmed.stockapp.Dom.dto.TransactionSummary;
import com.softmed.stockapp.Dom.entities.Transactions;

import java.util.List;


public class TransactionsListViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public TransactionsListViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());

    }

    public LiveData<List<TransactionSummary>> getTransactionSummaryList() {
        return appDatabase.transactionsDao().getTransactionSummary();
    }


    public LiveData<List<TransactionSummary>> getReceivedStockSummaryList() {
        return appDatabase.transactionsDao().getTransactionSummary();
    }

    public LiveData<List<Transactions>> getTransactionsListByProductId(int productId) {
        return appDatabase.transactionsDao().getLiveTransactionsByProductId(productId);
    }

    public LiveData<Transactions> getLastTransactionByProductId(int productId) {
        return appDatabase.transactionsDao().getLastTransactionByProductId(productId);
    }

    private static class deleteAsyncTask extends AsyncTask<Transactions, Void, Void> {
        private AppDatabase db;
        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Transactions... params) {
            db.transactionsDao().deleteTransactions(params[0]);
            return null;
        }

    }

}
