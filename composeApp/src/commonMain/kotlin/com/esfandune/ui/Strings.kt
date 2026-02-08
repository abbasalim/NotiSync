package com.esfandune.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

enum class AppLanguage {
    FA,
    EN
}

fun AppLanguage.toggled(): AppLanguage = if (this == AppLanguage.FA) AppLanguage.EN else AppLanguage.FA

data class AppStrings(
    val appName: String,
    val filterAll: String,
    val notificationsCleared: String,
    val clearAllContentDescription: String,
    val silentEnabled: String,
    val silentDisabled: String,
    val enableNotifications: String,
    val disableNotifications: String,
    val help: String,
    val about: String,
    val showQr: String,
    val emptyNotifications: String,
    val copied: String,
    val qrTitle: String,
    val qrContentDescription: String,
    val close: String,
    val helpTitle: String,
    val helpStep0: String,
    val helpStep1Title: String,
    val helpStep1Body: String,
    val helpDesktopImageDesc: String,
    val helpStep2Title: String,
    val helpStep2Body: String,
    val helpMobileImageDesc: String,
    val helpStep3Title: String,
    val helpStep3Body: String,
    val helpUnderstood: String,
    val deleteNotification: String,
    val copyToClipboard: String,
    val trayHide: String,
    val trayShow: String,
    val trayExit: String,
    val loading: String,
    val english: String,
    val persian: String
)

private fun stringsFa() = AppStrings(
    appName = "NotiSync",
    filterAll = "همه",
    notificationsCleared = "تمامی اعلان‌ها پاک شدند",
    clearAllContentDescription = "پاک کردن همه",
    silentEnabled = "حالت سکوت فعال شد",
    silentDisabled = "حالت سکوت غیرفعال شد",
    enableNotifications = "فعال کردن نوتیف",
    disableNotifications = "غیرفعال کردن نوتیف",
    help = "راهنما",
    about = "درباره",
    showQr = "نمایش QR Code",
    emptyNotifications = "هیچ نوتیفیکیشنی وجود ندارد",
    copied = "متن کپی شد",
    qrTitle = "QR Code",
    qrContentDescription = "QR Code",
    close = "بستن",
    helpTitle = "راهنمای کار با برنامه",
    helpStep0 = "0. برنامه دسکتاپ را از سایت زیر نصب نمایید:",
    helpStep1Title = "1. راهنمای نصب و راه‌اندازی دسکتاپ:",
    helpStep1Body = " برنامه دسکتاپ را اجرا کرده، برروی تنظیمات بزنید و برروی علامت بارکد کلیک کنید.",
    helpDesktopImageDesc = "راهنمای دسکتاپ",
    helpStep2Title = "2. اتصال دستگاه موبایل:",
    helpStep2Body = " در برنامه موبایل، برروی تنظیمات زده و علامت بارکد را لمس کنید تا دوربین باز شده و آن را جلو بارکد قرار دهید. بعد از تکمیل آی پی و پورت برروی علامت (+) جهت ذخیره کلیک نمایید.",
    helpMobileImageDesc = "راهنمای موبایل",
    helpStep3Title = "3. شروع استفاده:",
    helpStep3Body = "در صورت اتصال موفق، می‌توانید از قابلیت‌های برنامه استفاده کنید. اعلان‌های شما به صورت خودکار همگام‌سازی می‌شوند.",
    helpUnderstood = "متوجه شدم",
    deleteNotification = "حذف اعلان",
    copyToClipboard = "کپی در کلیپ‌بورد",
    trayHide = "مخفی کردن",
    trayShow = "نمایش برنامه",
    trayExit = "خروج کامل",
    loading = "در حال بارگذاری...",
    english = "Eng",
    persian = "فا"
)

private fun stringsEn() = AppStrings(
    appName = "NotiSync",
    filterAll = "All",
    notificationsCleared = "All notifications cleared",
    clearAllContentDescription = "Clear all",
    silentEnabled = "Silent mode enabled",
    silentDisabled = "Silent mode disabled",
    enableNotifications = "Enable notifications",
    disableNotifications = "Disable notifications",
    help = "Help",
    about = "About",
    showQr = "Show QR code",
    emptyNotifications = "No notifications",
    copied = "Copied",
    qrTitle = "QR Code",
    qrContentDescription = "QR Code",
    close = "Close",
    helpTitle = "App guide",
    helpStep0 = "0. Install the desktop app from:",
    helpStep1Title = "1. Desktop setup:",
    helpStep1Body = "Run the desktop app, open settings, and click the barcode icon.",
    helpDesktopImageDesc = "Desktop guide",
    helpStep2Title = "2. Connect mobile device:",
    helpStep2Body = "In the mobile app, open settings and tap the barcode icon to open the camera. Point it at the QR code, then tap (+) to save the IP and port.",
    helpMobileImageDesc = "Mobile guide",
    helpStep3Title = "3. Start using:",
    helpStep3Body = "When connected successfully, you can use the app features. Your notifications will sync automatically.",
    helpUnderstood = "Got it",
    deleteNotification = "Delete notification",
    copyToClipboard = "Copy to clipboard",
    trayHide = "Hide",
    trayShow = "Show app",
    trayExit = "Exit",
    loading = "Loading...",
    english = "Eng",
    persian = "فا"
)

fun stringsFor(language: AppLanguage): AppStrings = when (language) {
    AppLanguage.FA -> stringsFa()
    AppLanguage.EN -> stringsEn()
}

val LocalAppStrings = staticCompositionLocalOf { stringsFa() }

@Composable
fun ProvideAppStrings(
    language: AppLanguage,
    content: @Composable () -> Unit
) {
    val strings = remember(language) { stringsFor(language) }
    CompositionLocalProvider(LocalAppStrings provides strings, content = content)
}
