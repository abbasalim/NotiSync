package com.esfandune.util

fun String.packageToEmoji(): String {
    return when(this) {
        // پیام رسان‌ها
        "com.google.android.apps.messaging" -> "✉️"
        "app.rbmain.a", "org.telegram.messenger", "ir.eitaa.messenger", "com.whatsapp" -> "💬"
        "com.facebook.orca" -> "💬" // Messenger
        "com.viber.voip" -> "💬" // Viber
        "com.skype.raider" -> "💬" // Skype
        "com.discord" -> "🎮" // Discord
        "com.snapchat.android" -> "👻"
        "jp.naver.line.android" -> "💬"
        "com.kakao.talk" -> "💬"
        "org.thoughtcrime.securesms" -> "🔒" // Signal
        "ir.nasim.android" -> "💬" // ناسیم
        "ir.bale.messenger" -> "💬" // بله
        "ir.gap.android" -> "💬" // گپ
        "ir.soroush.messenger" -> "💬" // سروش
        "ir.shad.android" -> "🎓" // شاد

        // شبکه‌های اجتماعی
        "com.facebook.katana" -> "📘" // Facebook
        "com.instagram.android" -> "📷"
        "com.twitter.android" -> "🐦"
        "com.linkedin.android" -> "💼"
        "com.pinterest" -> "📌"
        "com.reddit.frontpage" -> "🤖"
        "com.tumblr" -> "📝"
        "com.zhiliaoapp.musically" -> "🎵" // TikTok
        "com.ss.android.ugc.trill" -> "🎵" // TikTok Lite

        // ویدیو و سرگرمی
        "com.google.android.youtube" -> "▶️"
        "com.netflix.mediaclient" -> "🎬"
        "com.amazon.avod.thirdpartyclient" -> "📺" // Prime Video
        "com.disney.disneyplus" -> "🏰"
        "com.spotify.music" -> "🎵"
        "com.google.android.apps.youtube.music" -> "🎶"
        "com.soundcloud.android" -> "☁️"
        "fm.castbox.audiobook.radio.podcast" -> "🎧"
        "com.fidibo.android" -> "📚" // فیدیبو
        "ir.tebyan.android" -> "📖" // تبیان
        "ir.namasha.mobile" -> "🎬" // نماشا
        "ir.aio.app" -> "📺" // اپارات
        "ir.vista.android.filmnet" -> "🎥" // فیلم نت
        "ir.filimo.video" -> "🎬" // فیلیمو
        "ir.radio.radiojavan" -> "🎵" // رادیو جوان
        "ir.nav.beeptounes.android" -> "🎵" // بیپ تونز
        "com.pishgamsoft.anten" -> "📻" // آنتن
        "ir.behtarino.app" -> "🎯" // بهترینو
        "ir.mehrandish.ofoq" -> "🌙" // افق
        "ir.tiwall.client" -> "🎮" // تی وال
        "ir.game8.android" -> "🎮" // گیم ایت

        // بازی‌ها
        "com.supercell.clashofclans" -> "⚔️"
        "com.king.candycrushsaga" -> "🍬"
        "com.mojang.minecraftpe" -> "⛏️"
        "com.roblox.client" -> "🎮"
        "com.epicgames.fortnite" -> "🎯"
        "com.ea.gp.fifamobile" -> "⚽"
        "com.garena.game.freefire" -> "🔫"
        "com.pubg.imobile" -> "🎯"

        // خرید و فروشگاه
        "com.amazon.mShop.android.shopping" -> "📦"
        "com.ebay.mobile" -> "🛒"
        "com.alibaba.aliexpresshd" -> "🛍️"
        "com.digikala.android" -> "🛒"
        "com.snapp.food" -> "🍕"
        "ir.tapsell.plus.sdk.unity.sample" -> "🛒"
        "ir.bamilo.store" -> "🛍️" // بامیلو
        "com.torob.android" -> "🔍" // ترب
        "ir.cafebazaar.pardakht" -> "💳" // کافه بازار پرداخت
        "com.cafebazaar.bazaar" -> "📱" // کافه بازار
        "ir.myket" -> "📱" // مایکت
        "ir.achareh.android.customer" -> "🔧" // آچاره
        "ir.snapp.market" -> "🛒" // اسنپ مارکت
        "ir.okcs.alopeyk" -> "🚚" // الوپیک
        "ir.divar" -> "🏪" // دیوار
        "ir.sheypoor.mobile" -> "🏠" // شیپور

        // نقشه و حمل و نقل
        "com.google.android.apps.maps" -> "🗺️"
        "com.waze" -> "🚗"
        "com.ubercab" -> "🚗"
        "ir.snapp.taxi" -> "🚕"
        "ir.tapsi.client" -> "🚖"

        // بانکی و پرداخت
        "com.paypal.android.p2pmobile" -> "💳"
        "com.google.android.apps.walletnfcrel" -> "💰" // Google Pay
        "ir.shaparak.pec.sb24" -> "💳" // Saman Bank
        "com.ghoghnoos.bmidepositaccounts" -> "🏦" // BMI
        "ir.co.samanbank.mobilebank" -> "🏦"
        "ir.bmi.scb.mobile.android" -> "🏦" // بانک صنعت و معدن
        "ir.mci.ecareapp" -> "📞" // همراه من
        "ir.irancell.selfcare" -> "📱" // ایرانسل من
        "com.rightel.app" -> "📱" // رایتل
        "ir.asan.pardakht" -> "💳" // آسان پرداخت
        "ir.sep.wallet.app" -> "💰" // کیف پول سپ
        "com.zarinpal.android" -> "💳" // زرین پال
        "ir.jibit.wallet" -> "💰" // جیبیت
        "ir.parsian.mobile.pna" -> "🏦" // پارسیان
        "ir.bank.mellat.android" -> "🏦" // ملت
        "ir.postbank.mobile.android" -> "🏦" // پست بانک
        "ir.tejarat.mobile" -> "🏦" // تجارت
        "ir.refah.bank.mobile" -> "🏦" // رفاه
        "ir.maskan.mobile" -> "🏠" // مسکن
        "ir.keshavarzi.android" -> "🏦" // کشاورزی
        "ir.saderat.mobile" -> "🏦" // صادرات
        "ir.melli.mobile.android" -> "🏦" // ملی
        "com.dey.bank.android" -> "🏦" // دی
        "ir.pasargad.mobile.android" -> "🏦" // پاسارگاد

        // کاری و بهره‌وری
        "com.microsoft.office.outlook" -> "📧"
        "com.google.android.gm" -> "📬" // Gmail
        "com.microsoft.office.word" -> "📄"
        "com.microsoft.office.excel" -> "📊"
        "com.microsoft.office.powerpoint" -> "📈"
        "com.adobe.reader" -> "📑"
        "com.dropbox.android" -> "☁️"
        "com.google.android.apps.docs" -> "📝" // Google Docs
        "com.slack" -> "💼"
        "us.zoom.videomeetings" -> "📹"

        // عکاسی و ویرایش
        "com.adobe.photoshopmobile" -> "🎨"
        "com.vsco.cam" -> "📸"
        "com.lightricks.facetune" -> "✨"
        "com.niksoftware.snapseed" -> "📷"
        "com.canva.editor" -> "🎨"

        // آموزش
        "com.duolingo" -> "🦉"
        "com.khanacademy.android" -> "🎓"
        "com.coursera.android" -> "📚"
        "com.udemy.android" -> "💡"
        "ir.schoolsalam" -> "🎓" // مدرسه سلام
        "ir.lms.navid" -> "📖" // سامانه ناوید
        "ir.amoozesh.android" -> "📚" // آموزش
        "ir.ostadkr.ostad" -> "👨‍🏫" // استاد
        "ir.quran.app" -> "📖" // قرآن
        "ir.hozehonline.app" -> "📚" // حوزه آنلاین
        "ir.maaref.android" -> "📖" // معارف
        "ir.roshd.app" -> "🌱" // رشد

        // سلامت و ورزش
        "com.myfitnesspal.android" -> "💪"
        "com.nike.plusone" -> "👟"
        "com.adidas.app" -> "⚽"
        "com.samsung.android.app.shealth" -> "❤️"
        "com.google.android.apps.fitness" -> "🏃"

        // خبر و اطلاعات
        "flipboard.app" -> "📰"
        "com.google.android.apps.magazines" -> "📖" // Google News
        "bbc.mobile.news.ww" -> "📺"
        "com.cnn.mobile.android.phone" -> "📺"
        "ir.irib.news" -> "📺" // خبرگزاری صداوسیما
        "ir.tasnimnews.android" -> "📰" // تسنیم
        "ir.irinn.mobile" -> "📺" // شبکه خبر
        "ir.press.tv" -> "📺" // پرس تی وی
        "ir.farsnews.android" -> "📰" // فارس
        "ir.mehrnews.app" -> "📰" // مهر
        "ir.isna.mobile" -> "📰" // ایسنا
        "ir.khabaronline.app" -> "📰" // خبرآنلاین
        "ir.entekhab.mobile" -> "📰" // انتخاب
        "ir.donyayeeqtesad.mobile" -> "📊" // دنیای اقتصاد
        "ir.mashreghnews.app" -> "📰" // مشرق

        // ابزارها
        "com.google.android.apps.translate" -> "🌐"
        "com.google.android.calculator" -> "🔢"
        "com.android.chrome" -> "🌐"
        "org.mozilla.firefox" -> "🦊"
        "com.microsoft.emmx" -> "🌐" // Edge
        "com.opera.browser" -> "🌐"
        "ir.hamrahbank.android" -> "💳" // همراه بانک
        "ir.post.postbarcode" -> "📦" // پست بارکد
        "ir.srtelecom.android" -> "📡" // شرکت مخابرات
        "ir.sepehr.mobile.android" -> "🌟" // سپهر
        "ir.sanjagh.android" -> "⚖️" // سنجاق
        "ir.saipa.app" -> "🚗" // سایپا
        "ir.ikco.mobile" -> "🚗" // ایران خودرو
        "ir.rahyab.android" -> "🧭" // راهیاب
        "ir.balad.app" -> "🗺️" // بلد
        "ir.neshan.traffic" -> "🗺️" // نشان
        "ir.cedarmaps.app" -> "🗺️" // سدار مپس
        "ir.meteofa.android" -> "🌤️" // هواشناسی
        "ir.mcls.mcalculator" -> "🔢" // ماشین حساب
        "ir.calendar.app" -> "📅" // تقویم
        "ir.ptime.android" -> "📅" // تقویم فارسی
        "ir.app.bourse" -> "📈" // بورس
        "ir.tgju.android" -> "💰" // طلا و ارز

        else -> ""
    }
}