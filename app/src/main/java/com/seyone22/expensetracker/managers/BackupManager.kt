package com.seyone22.expensetracker.managers

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import com.seyone22.expensetracker.data.MMEXDatabase
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.BillsDeposits
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.Report
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.toBillsDeposit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream

class BackupManager(private val context: Context) {
    private val db = MMEXDatabase.getDatabase(context)

    suspend fun exportBackup(outputStream: OutputStream): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject()

            // 1. Export Accounts
            val accountsList = db.accountDao().getAllAccounts().first()
            val accountsArray = JSONArray()
            accountsList.forEach { acc ->
                val obj = JSONObject().apply {
                    put("accountId", acc.accountId)
                    put("accountName", acc.accountName)
                    put("accountType", acc.accountType)
                    put("accountNum", acc.accountNum ?: "")
                    put("status", acc.status)
                    put("notes", acc.notes ?: "")
                    put("heldAt", acc.heldAt ?: "")
                    put("website", acc.website ?: "")
                    put("contactInfo", acc.contactInfo ?: "")
                    put("accessInfo", acc.accessInfo ?: "")
                    put("initialBalance", acc.initialBalance ?: 0.0)
                    put("initialDate", acc.initialDate ?: "")
                    put("favoriteAccount", acc.favoriteAccount)
                    put("currencyId", acc.currencyId)
                    put("statementLocked", acc.statementLocked ?: 0)
                    put("statementDate", acc.statementDate ?: "")
                    put("minimumBalance", acc.minimumBalance ?: 0.0)
                    put("creditLimit", acc.creditLimit ?: 0.0)
                    put("interestRate", acc.interestRate ?: 0.0)
                    put("paymentDueDate", acc.paymentDueDate ?: "")
                    put("minimumPayment", acc.minimumPayment ?: 0.0)
                }
                accountsArray.put(obj)
            }
            root.put("accounts", accountsArray)

            // 2. Export Transactions
            val transactionsList = db.transactionDao().getAllRawTransactions().first()
            val transactionsArray = JSONArray()
            transactionsList.forEach { tr ->
                val obj = JSONObject().apply {
                    put("transId", tr.transId)
                    put("accountId", tr.accountId)
                    put("toAccountId", tr.toAccountId ?: -1)
                    put("payeeId", tr.payeeId)
                    put("transCode", tr.transCode)
                    put("transAmount", tr.transAmount)
                    put("status", tr.status)
                    put("transactionNumber", tr.transactionNumber ?: "")
                    put("notes", tr.notes ?: "")
                    put("categoryId", tr.categoryId)
                    put("transDate", tr.transDate)
                    put("followUpId", tr.followUpId)
                    put("toTransAmount", tr.toTransAmount)
                    put("color", tr.color)
                }
                transactionsArray.put(obj)
            }
            root.put("transactions", transactionsArray)

            // 3. Export Categories
            val categoriesList = db.categoryDao().getAllCategories().first()
            val categoriesArray = JSONArray()
            categoriesList.forEach { cat ->
                val obj = JSONObject().apply {
                    put("categId", cat.categId)
                    put("categName", cat.categName)
                    put("parentId", cat.parentId)
                }
                categoriesArray.put(obj)
            }
            root.put("categories", categoriesArray)

            // 4. Export Payees
            val payeesList = db.payeeDao().getAllPayees().first()
            val payeesArray = JSONArray()
            payeesList.forEach { pay ->
                val obj = JSONObject().apply {
                    put("payeeId", pay.payeeId)
                    put("payeeName", pay.payeeName)
                    put("categId", pay.categId)
                    put("number", pay.number)
                    put("website", pay.website)
                    put("notes", pay.notes)
                    put("active", pay.active)
                }
                payeesArray.put(obj)
            }
            root.put("payees", payeesArray)

            // 5. Export Currencies
            val currenciesList = db.currencyFormatDao().getAllCurrencyFormats().first()
            val currenciesArray = JSONArray()
            currenciesList.forEach { cur ->
                val obj = JSONObject().apply {
                    put("currencyId", cur.currencyId)
                    put("currencyName", cur.currencyName)
                    put("pfx_symbol", cur.pfx_symbol)
                    put("sfx_symbol", cur.sfx_symbol)
                    put("decimal_point", cur.decimal_point)
                    put("group_seperator", cur.group_seperator)
                    put("unit_name", cur.unit_name)
                    put("cent_name", cur.cent_name)
                    put("scale", cur.scale)
                    put("baseConvRate", cur.baseConvRate)
                    put("currency_symbol", cur.currency_symbol)
                    put("currency_type", cur.currency_type)
                }
                currenciesArray.put(obj)
            }
            root.put("currencies", currenciesArray)

            // 6. Export BillsDeposits
            val billsList = db.billsDepositsDao().getAllBillsDeposits().first()
            val billsArray = JSONArray()
            billsList.forEach { bdDetails ->
                val bd = bdDetails.toBillsDeposit()
                val obj = JSONObject().apply {
                    put("BDID", bd.BDID)
                    put("ACCOUNTID", bd.ACCOUNTID)
                    put("TOACCOUNTID", bd.TOACCOUNTID ?: -1)
                    put("PAYEEID", bd.PAYEEID)
                    put("TRANSCODE", bd.TRANSCODE)
                    put("TRANSAMOUNT", bd.TRANSAMOUNT)
                    put("STATUS", bd.STATUS ?: "")
                    put("TRANSACTIONNUMBER", bd.TRANSACTIONNUMBER ?: "")
                    put("NOTES", bd.NOTES ?: "")
                    put("CATEGID", bd.CATEGID ?: -1)
                    put("TRANSDATE", bd.TRANSDATE ?: "")
                    put("FOLLOWUPID", bd.FOLLOWUPID ?: 0)
                    put("TOTRANSAMOUNT", bd.TOTRANSAMOUNT ?: bd.TRANSAMOUNT)
                    put("REPEATS", bd.REPEATS ?: 0)
                    put("NEXTOCCURRENCEDATE", bd.NEXTOCCURRENCEDATE ?: "")
                    put("NUMOCCURRENCES", bd.NUMOCCURRENCES ?: 0)
                    put("COLOR", bd.COLOR)
                }
                billsArray.put(obj)
            }
            root.put("billsDeposits", billsArray)

            // 7. Export Reports
            val reportsList = db.reportDao().getAllReports().first()
            val reportsArray = JSONArray()
            reportsList.forEach { rp ->
                val obj = JSONObject().apply {
                    put("REPORTID", rp.REPORTID)
                    put("REPORTNAME", rp.REPORTNAME)
                    put("GROUPNAME", rp.GROUPNAME ?: "")
                    put("ACTIVE", rp.ACTIVE)
                    put("SQLCONTENT", rp.SQLCONTENT ?: "")
                    put("LUACONTENT", rp.LUACONTENT ?: "")
                    put("TEMPLATECONTENT", rp.TEMPLATECONTENT ?: "")
                    put("DESCRIPTION", rp.DESCRIPTION ?: "")
                }
                reportsArray.put(obj)
            }
            root.put("reports", reportsArray)

            outputStream.use { out ->
                out.write(root.toString(2).toByteArray(Charsets.UTF_8))
                out.flush()
            }
            true
        } catch (e: Exception) {
            Log.e("BackupManager", "Export failed", e)
            false
        }
    }

    suspend fun importBackup(inputStream: InputStream): Boolean = withContext(Dispatchers.IO) {
        try {
            val jsonString = inputStream.use { stream ->
                stream.readBytes().toString(Charsets.UTF_8)
            }
            val root = JSONObject(jsonString)

            db.withTransaction {
                // Clear all tables
                db.clearAllTables()

                // 1. Import Currencies
                val currenciesArray = root.optJSONArray("currencies")
                if (currenciesArray != null) {
                    for (i in 0 until currenciesArray.length()) {
                        val obj = currenciesArray.getJSONObject(i)
                        val cur = CurrencyFormat(
                            currencyId = obj.getInt("currencyId"),
                            currencyName = obj.getString("currencyName"),
                            pfx_symbol = obj.optString("pfx_symbol", ""),
                            sfx_symbol = obj.optString("sfx_symbol", ""),
                            decimal_point = obj.optString("decimal_point", ""),
                            group_seperator = obj.optString("group_seperator", ""),
                            unit_name = obj.optString("unit_name", ""),
                            cent_name = obj.optString("cent_name", ""),
                            scale = obj.optInt("scale", 0),
                            baseConvRate = obj.optDouble("baseConvRate", 1.0),
                            currency_symbol = obj.optString("currency_symbol", ""),
                            currency_type = obj.optString("currency_type", "")
                        )
                        db.currencyFormatDao().insert(cur)
                    }
                }

                // 2. Import Categories
                val categoriesArray = root.optJSONArray("categories")
                if (categoriesArray != null) {
                    for (i in 0 until categoriesArray.length()) {
                        val obj = categoriesArray.getJSONObject(i)
                        val cat = Category(
                            categId = obj.getInt("categId"),
                            categName = obj.getString("categName"),
                            parentId = obj.optInt("parentId", -1)
                        )
                        db.categoryDao().insert(cat)
                    }
                }

                // 3. Import Payees
                val payeesArray = root.optJSONArray("payees")
                if (payeesArray != null) {
                    for (i in 0 until payeesArray.length()) {
                        val obj = payeesArray.getJSONObject(i)
                        val pay = Payee(
                            payeeId = obj.getInt("payeeId"),
                            payeeName = obj.getString("payeeName"),
                            categId = obj.optInt("categId", -1),
                            number = obj.optString("number", ""),
                            website = obj.optString("website", ""),
                            notes = obj.optString("notes", ""),
                            active = obj.optInt("active", 1)
                        )
                        db.payeeDao().insert(pay)
                    }
                }

                // 4. Import Accounts
                val accountsArray = root.optJSONArray("accounts")
                if (accountsArray != null) {
                    for (i in 0 until accountsArray.length()) {
                        val obj = accountsArray.getJSONObject(i)
                        val acc = Account(
                            accountId = obj.getInt("accountId"),
                            accountName = obj.getString("accountName"),
                            accountType = obj.getString("accountType"),
                            accountNum = obj.optString("accountNum", ""),
                            status = obj.getString("status"),
                            notes = obj.optString("notes", ""),
                            heldAt = obj.optString("heldAt", ""),
                            website = obj.optString("website", ""),
                            contactInfo = obj.optString("contactInfo", ""),
                            accessInfo = obj.optString("accessInfo", ""),
                            initialBalance = obj.optDouble("initialBalance", 0.0),
                            initialDate = obj.optString("initialDate", ""),
                            favoriteAccount = obj.optString("favoriteAccount", ""),
                            currencyId = obj.getInt("currencyId"),
                            statementLocked = obj.optInt("statementLocked", 0),
                            statementDate = obj.optString("statementDate", ""),
                            minimumBalance = obj.optDouble("minimumBalance", 0.0),
                            creditLimit = obj.optDouble("creditLimit", 0.0),
                            interestRate = obj.optDouble("interestRate", 0.0),
                            paymentDueDate = obj.optString("paymentDueDate", ""),
                            minimumPayment = obj.optDouble("minimumPayment", 0.0)
                        )
                        db.accountDao().insert(acc)
                    }
                }

                // 5. Import Transactions
                val transactionsArray = root.optJSONArray("transactions")
                if (transactionsArray != null) {
                    for (i in 0 until transactionsArray.length()) {
                        val obj = transactionsArray.getJSONObject(i)
                        val tr = Transaction(
                            transId = obj.getInt("transId"),
                            accountId = obj.getInt("accountId"),
                            toAccountId = obj.optInt("toAccountId", -1),
                            payeeId = obj.getInt("payeeId"),
                            transCode = obj.getString("transCode"),
                            transAmount = obj.getDouble("transAmount"),
                            status = obj.getString("status"),
                            transactionNumber = obj.optString("transactionNumber", ""),
                            notes = obj.optString("notes", ""),
                            categoryId = obj.getInt("categoryId"),
                            transDate = obj.getString("transDate"),
                            followUpId = obj.optInt("followUpId", 0),
                            toTransAmount = obj.optDouble("toTransAmount", 0.0),
                            color = obj.optInt("color", -1)
                        )
                        db.transactionDao().insert(tr)
                    }
                }

                // 6. Import BillsDeposits
                val billsArray = root.optJSONArray("billsDeposits")
                if (billsArray != null) {
                    for (i in 0 until billsArray.length()) {
                        val obj = billsArray.getJSONObject(i)
                        val bd = BillsDeposits(
                            BDID = obj.getInt("BDID"),
                            ACCOUNTID = obj.getInt("ACCOUNTID"),
                            TOACCOUNTID = obj.optInt("TOACCOUNTID", -1),
                            PAYEEID = obj.getInt("PAYEEID"),
                            TRANSCODE = obj.getString("TRANSCODE"),
                            TRANSAMOUNT = obj.getDouble("TRANSAMOUNT"),
                            STATUS = obj.optString("STATUS", ""),
                            TRANSACTIONNUMBER = obj.optString("TRANSACTIONNUMBER", ""),
                            NOTES = obj.optString("NOTES", ""),
                            CATEGID = obj.optInt("CATEGID", -1),
                            TRANSDATE = obj.optString("TRANSDATE", ""),
                            FOLLOWUPID = obj.optInt("FOLLOWUPID", 0),
                            TOTRANSAMOUNT = obj.optDouble("TOTRANSAMOUNT", 0.0),
                            REPEATS = obj.optInt("REPEATS", 0),
                            NEXTOCCURRENCEDATE = obj.optString("NEXTOCCURRENCEDATE", ""),
                            NUMOCCURRENCES = obj.optInt("NUMOCCURRENCES", 0),
                            COLOR = obj.optInt("COLOR", -1)
                        )
                        db.billsDepositsDao().insert(bd)
                    }
                }

                // 7. Import Reports
                val reportsArray = root.optJSONArray("reports")
                if (reportsArray != null) {
                    for (i in 0 until reportsArray.length()) {
                        val obj = reportsArray.getJSONObject(i)
                        val rp = Report(
                            REPORTID = obj.getInt("REPORTID"),
                            REPORTNAME = obj.getString("REPORTNAME"),
                            GROUPNAME = obj.optString("GROUPNAME", ""),
                            ACTIVE = obj.getInt("ACTIVE"),
                            SQLCONTENT = obj.optString("SQLCONTENT", ""),
                            LUACONTENT = obj.optString("LUACONTENT", ""),
                            TEMPLATECONTENT = obj.optString("TEMPLATECONTENT", ""),
                            DESCRIPTION = obj.optString("DESCRIPTION", "")
                        )
                        db.reportDao().insert(rp)
                    }
                }
            }
            true
        } catch (e: Exception) {
            Log.e("BackupManager", "Import failed", e)
            false
        }
    }
}
