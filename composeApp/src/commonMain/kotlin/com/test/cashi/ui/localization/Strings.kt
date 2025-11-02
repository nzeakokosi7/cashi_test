package com.test.cashi.ui.localization

/**
 * Localization support for Cashi Payment App
 * Supports English and Arabic (for Sudanese users)
 */
interface Strings {
    // App name
    val appName: String

    // Transaction List Screen
    val transactions: String
    val sendPayment: String
    val noTransactionsYet: String
    val emptyStateDescription: String
    val sendFirstPayment: String

    // Payment Form
    val recipientEmail: String
    val recipientEmailPlaceholder: String
    val amount: String
    val amountPlaceholder: String
    val currency: String
    val selectCurrency: String
    val submit: String
    val cancel: String

    // Messages
    val paymentSent: String
    val paymentFailed: String
    val loading: String
    val invalidEmail: String
    val invalidAmount: String

    // Currency names
    val currencyUsd: String
    val currencyEur: String
}

/**
 * English strings
 */
object EnglishStrings : Strings {
    override val appName = "Cashi"
    override val transactions = "Transactions"
    override val sendPayment = "Send Payment"
    override val noTransactionsYet = "No Transactions Yet"
    override val emptyStateDescription = "Start sending money to friends and family.\nYour transactions will appear here."
    override val sendFirstPayment = "Send Your First Payment"

    override val recipientEmail = "Recipient Email"
    override val recipientEmailPlaceholder = "example@email.com"
    override val amount = "Amount"
    override val amountPlaceholder = "0.00"
    override val currency = "Currency"
    override val selectCurrency = "Select Currency"
    override val submit = "Submit"
    override val cancel = "Cancel"

    override val paymentSent = "Payment sent successfully"
    override val paymentFailed = "Payment failed"
    override val loading = "Loading..."
    override val invalidEmail = "Invalid email address"
    override val invalidAmount = "Amount must be greater than 0"

    override val currencyUsd = "US Dollar (USD)"
    override val currencyEur = "Euro (EUR)"
}

/**
 * Arabic strings (for Sudanese users)
 * Note: Arabic text should be displayed right-to-left (RTL)
 */
object ArabicStrings : Strings {
    override val appName = "كاشي"
    override val transactions = "المعاملات"
    override val sendPayment = "إرسال دفعة"
    override val noTransactionsYet = "لا توجد معاملات بعد"
    override val emptyStateDescription = "ابدأ بإرسال الأموال للأصدقاء والعائلة.\nستظهر معاملاتك هنا."
    override val sendFirstPayment = "أرسل دفعتك الأولى"

    override val recipientEmail = "البريد الإلكتروني للمستلم"
    override val recipientEmailPlaceholder = "example@email.com"
    override val amount = "المبلغ"
    override val amountPlaceholder = "0.00"
    override val currency = "العملة"
    override val selectCurrency = "اختر العملة"
    override val submit = "إرسال"
    override val cancel = "إلغاء"

    override val paymentSent = "تم إرسال الدفعة بنجاح"
    override val paymentFailed = "فشل إرسال الدفعة"
    override val loading = "جار التحميل..."
    override val invalidEmail = "عنوان بريد إلكتروني غير صالح"
    override val invalidAmount = "يجب أن يكون المبلغ أكبر من 0"

    override val currencyUsd = "دولار أمريكي (USD)"
    override val currencyEur = "يورو (EUR)"
}

/**
 * Supported locales
 */
enum class SupportedLocale(val code: String, val strings: Strings) {
    ENGLISH("en", EnglishStrings),
    ARABIC("ar", ArabicStrings);

    companion object {
        fun fromCode(code: String): SupportedLocale {
            return entries.find { it.code == code } ?: ENGLISH
        }
    }
}