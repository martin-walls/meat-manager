package com.martinwalls.nea.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Contract;
import com.martinwalls.nea.data.models.ProductQuantity;

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
            int reminderDaysBefore = contract.getReminder();
            if (contract.getDaysToNextRepeat() == reminderDaysBefore) {
                String title = "Upcoming contract in " + reminderDaysBefore + " days"; //todo string resources (plural)

                StringBuilder builder = new StringBuilder();
//                for (ProductQuantity productQuantity : contract.getProductList()) {
//                    builder.append(productQuantity.getProduct().getProductName());
//                    builder.append(", ");
//                }

                List<ProductQuantity> productList = contract.getProductList();
                for (int i = 0; i < productList.size(); i++) {
                    builder.append(productList.get(i).getProduct().getProductName());
                    if (i != productList.size() - 1) {
                        builder.append(", ");
                    }
                }
                String text = builder.toString();

                NotificationUtils.sendNotification(context, context.getString(R.string.channel_reminder_id),
                        title, text, R.drawable.ic_date, 1);
            }
        }


//        ReminderUtils.showTestNotification(context);

//        Calendar today = Calendar.getInstance();

//        today.add(Calendar.MINUTE, 5);

//        ReminderUtils.scheduleReminder(context, today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE));
    }
}
