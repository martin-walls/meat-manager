package com.martinwalls.nea.util.notification;

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

    /**
     * Shows a reminder for each contract that is upcoming. Checks for each
     * contract whether the number of days it is away is the same as the
     * chosen reminder time. Schedules the next reminder for the next day.
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        DBHandler dbHandler = new DBHandler(context);

        List<Contract> contractList = dbHandler.getAllContracts();

        // show a notification for each upcoming contract
        for (Contract contract : contractList) {
            int reminderDaysBefore = contract.getReminder();
            if (contract.getDaysToNextRepeat() == reminderDaysBefore) {
                showNotificationForContract(context, contract);
            }
        }

        // schedule next reminder for next day
        ReminderUtils.scheduleReminderAtDefaultTime(context);
    }

    /**
     * Shows a notification for a contract, showing how many days in advance
     * it is, and which products it is for.
     */
    private void showNotificationForContract(Context context, Contract contract) {
        int reminderDaysBefore = contract.getReminder();
        String title = context.getResources().getQuantityString(R.plurals.contract_alert_upcoming_days,
                reminderDaysBefore, reminderDaysBefore);

        String text = getProductListDisplay(contract.getProductList());

        int notificationId = contract.getContractId();

        Intent onClickIntent = new Intent(context, ContractDetailActivity.class);
        onClickIntent.putExtra(ContractDetailActivity.EXTRA_CONTRACT_ID, contract.getContractId());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(onClickIntent);

        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationUtils.sendNotification(context, context.getString(R.string.channel_reminder_id),
                title, text, R.drawable.ic_alert,
                pendingIntent, notificationId, NotificationUtils.GROUP_CONTRACT_REMINDER);
    }

    /**
     * Gets a string representation of all the products connected to a
     * contract, comma separated, to display in the body of the notification.
     */
    private String getProductListDisplay(List<ProductQuantity> productList) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < productList.size(); i++) {
            builder.append(productList.get(i).getProduct().getProductName());
            if (i != productList.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }
}
