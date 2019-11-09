package com.martinwalls.nea.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.TaskStackBuilder;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Contract;
import com.martinwalls.nea.data.models.ProductQuantity;
import com.martinwalls.nea.ui.contracts.ContractDetailActivity;

import java.util.List;

public class ReminderReceiver extends BroadcastReceiver {

    @Deprecated
    public static final String EXTRA_TITLE = "title";
    @Deprecated
    public static final String EXTRA_TEXT = "text";
    @Deprecated
    public static final String EXTRA_NUM_ELAPSED = "num_elapsed";
    @Deprecated
    public static final String EXTRA_CONTRACT = "contract";

    @Override
    public void onReceive(Context context, Intent intent) {

        DBHandler dbHandler = new DBHandler(context);

        List<Contract> contractList = dbHandler.getAllContracts();



        for (Contract contract : contractList) {
            int reminderDaysBefore = contract.getReminder(); //todo handle null case (reminder off)
            if (contract.getDaysToNextRepeat() == reminderDaysBefore) {
                String title = context.getResources().getQuantityString(R.plurals.contract_upcoming_days,
                        reminderDaysBefore, reminderDaysBefore);

                StringBuilder builder = new StringBuilder();
                List<ProductQuantity> productList = contract.getProductList();
                for (int i = 0; i < productList.size(); i++) {
                    builder.append(productList.get(i).getProduct().getProductName());
                    if (i != productList.size() - 1) {
                        builder.append(", ");
                    }
                }
                String text = builder.toString();

                int notificationId = contract.getContractId();

                Intent onClickIntent = new Intent(context, ContractDetailActivity.class);
                onClickIntent.putExtra(ContractDetailActivity.EXTRA_CONTRACT_ID, contract.getContractId());
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(onClickIntent);

                PendingIntent pendingIntent = stackBuilder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationUtils.sendNotification(context, context.getString(R.string.channel_reminder_id),
                        title, text, R.drawable.ic_date,
                        pendingIntent, notificationId, NotificationUtils.GROUP_CONTRACT_REMINDER);
            }
        }


//        ReminderUtils.showTestNotification(context);

//        Calendar today = Calendar.getInstance();

//        today.add(Calendar.MINUTE, 5);

//        ReminderUtils.scheduleReminder(context, today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE));
    }
}
