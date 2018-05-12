package com.apt_mgmt_sys.APIServer.services;


/****
 * PURPOSE: CRON job that runs every 1AM everyday.
 * (1) changes status of every PENDING transaction to OVERDUE
 * (2) execute every transaction creation policies.
 * x [t1, t2, t3] y
 * - set invalidate to transaction (create transaction amount 0, status: completed, remark; "paid with transaction (big payment)
 *
 *
 */


public class TransactionScheduler {
    /*
    *
    *
    * */
}
