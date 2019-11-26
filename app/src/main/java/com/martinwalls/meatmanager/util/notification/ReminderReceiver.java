package com.martinwalls.meatmanager.util.notification;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.TaskStackBuilder;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Contract;
import com.martinwalls.meatmanager.data.models.Order;
import com.martinwalls.meatmanager.data.models.ProductQuantity;
import com.martinwalls.meatmanager.ui.contracts.ContractDetailActivity;
import com.martinwalls.meatmanager.ui.orders.OrderDetailActivity;

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

        int notificationId = 1;

        // show a notification for each upcoming contract
        for (Contract contract : contractList) {
            int reminderDaysBefore = contract.getReminder();
            if (contract.getDaysToNextRepeat() == reminderDaysBefore) {
                showNotificationForContract(context, contract, notificationId);
                notificationId++;
            }
        }

        List<Order> orderList = dbHandler.getAllOrdersNotCompleted();

        for (Order order : orderList) {
            if (order.getDaysToOrderDate() == 1) {
                showNotificationForOrder(context, order, notificationId);
                notificationId++;
            }
        }

        // schedule next reminder for next day
        ReminderUtils.scheduleReminderAtDefaultTime(context);
    }

    /**
     * Shows a notification for a contract, showing how many days in advance
     * it is, and which products it is for.
     */
    private void showNotificationForContract(Context context, Contract contract, int id) {
        int reminderDaysBefore = contract.getReminder();
        String title = context.getResources().getQuantityString(
                R.plurals.contract_alert_upcoming_days,
                reminderDaysBefore, reminderDaysBefore);

        String text = getProductListDisplay(contract.getProductList());

        Intent onClickIntent = new Intent(context, ContractDetailActivity.class);
        onClickIntent.putExtra(ContractDetailActivity.EXTRA_CONTRACT_ID,
                contract.getContractId());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(onClickIntent);

        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationUtils.sendNotification(context,
                context.getString(R.string.channel_reminder_id),
                title, text, R.drawable.ic_alert,
                pendingIntent, id, NotificationUtils.GROUP_CONTRACT_REMINDER);
    }

    private void showNotificationForOrder(Context context, Order order, int id) {
        String title = context.getString(R.string.order_alert_upcoming_tomorrow);
        String text = getProductListDisplay(order.getProductList());

        Intent onClickIntent = new Intent(context, OrderDetailActivity.class);
        onClickIntent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.getOrderId());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(onClickIntent);

        PendingIntent pendingIntent
                = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationUtils.sendNotification(context,
                context.getString(R.string.channel_reminder_id),
                title, text, R.drawable.ic_alert,
                pendingIntent, id, NotificationUtils.GROUP_ORDER_REMINDER);
    }

    /**
     * Gets a string representation of all the products connected to a
     * contract/order, comma separated, to display in the body of the notification.
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
