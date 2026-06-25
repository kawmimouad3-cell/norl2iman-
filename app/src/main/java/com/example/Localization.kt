package com.example

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

enum class AppLanguage(val code: String, val displayName: String, val isRtl: Boolean) {
    AR("ar", "العربية", true),
    EN("en", "English", false),
    FR("fr", "Français", false);

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.firstOrNull { it.code == code } ?: AR
        }
    }
}

object AppTranslation {
    private val dictionary = mapOf(
        "app_name" to mapOf(
            "ar" to "نور الإيمان",
            "en" to "Noor Al-Iman",
            "fr" to "Noor Al-Iman"
        ),
        "ministry_standards" to mapOf(
            "ar" to "وفق معايير وزارة الأوقاف المغربية",
            "en" to "According to Moroccan Awqaf Ministry",
            "fr" to "Selon les normes du Ministère des Habous"
        ),
        "home" to mapOf(
            "ar" to "الرئيسية",
            "en" to "Home",
            "fr" to "Accueil"
        ),
        "quran" to mapOf(
            "ar" to "القرآن",
            "en" to "Quran",
            "fr" to "Coran"
        ),
        "search" to mapOf(
            "ar" to "البحث",
            "en" to "Search",
            "fr" to "Recherche"
        ),
        "adhkar" to mapOf(
            "ar" to "الأذكار",
            "en" to "Adhkar",
            "fr" to "Adhkar"
        ),
        "more" to mapOf(
            "ar" to "المزيد",
            "en" to "More",
            "fr" to "Plus"
        ),
        "prayer_times" to mapOf(
            "ar" to "أوقات الصلاة",
            "en" to "Prayer Times",
            "fr" to "Heures de prière"
        ),
        "last_read" to mapOf(
            "ar" to "آخر قراءة • Récemment lu",
            "en" to "Last Read",
            "fr" to "Dernière lecture"
        ),
        "continue" to mapOf(
            "ar" to "متابعة",
            "en" to "Continue",
            "fr" to "Continuer"
        ),
        "start" to mapOf(
            "ar" to "البدء",
            "en" to "Start",
            "fr" to "Démarrer"
        ),
        "no_read_yet" to mapOf(
            "ar" to "لم تبدأ القراءة بعد. اضغط هنا للبدء الآن.",
            "en" to "You haven't started reading yet. Click here to start.",
            "fr" to "Vous n'avez pas encore commencé. Cliquez ici pour commencer."
        ),
        "settings_title" to mapOf(
            "ar" to "إعدادات التذكير والتنبيه",
            "en" to "Alert Settings",
            "fr" to "Paramètres d'Alerte"
        ),
        "settings_desc" to mapOf(
            "ar" to "اختر المدة المفضلة للتنبيه المسبق قبل موعد الأذن للوضوء والاستعداد للذكر:",
            "en" to "Choose your preferred pre-prayer alert to prepare yourself:",
            "fr" to "Choisissez votre alerte de pré-prière préférée pour vous préparer :"
        ),
        "alarm_off" to mapOf(
            "ar" to "إيقاف التنبيه المسبق (أذان فقط)",
            "en" to "Adhan only (no pre-alarm)",
            "fr" to "Adhan uniquement (pas de pré-alarme)"
        ),
        "min_5" to mapOf(
            "ar" to "5 دقائق قبل الصلاة",
            "en" to "5 minutes before prayer",
            "fr" to "5 minutes avant la prière"
        ),
        "min_10" to mapOf(
            "ar" to "10 دقائق قبل الصلاة",
            "en" to "10 minutes before prayer",
            "fr" to "10 minutes avant la prière"
        ),
        "min_15" to mapOf(
            "ar" to "15 دقيقة قبل الصلاة",
            "en" to "15 minutes before prayer",
            "fr" to "15 minutes avant la prière"
        ),
        "min_30" to mapOf(
            "ar" to "30 دقيقة قبل الصلاة",
            "en" to "30 minutes before prayer",
            "fr" to "30 minutes avant la prière"
        ),
        "ok" to mapOf(
            "ar" to "موافق",
            "en" to "OK",
            "fr" to "OK"
        ),
        "about" to mapOf(
            "ar" to "حول تطبيق نور الإيمان",
            "en" to "About Noor Al-Iman",
            "fr" to "À propos de Noor Al-Iman"
        ),
        "about_desc" to mapOf(
            "ar" to "تطبيق إسلامي مغربي متكامل لعرض أوقات الصلوات والقرآن الكريم والأذكار",
            "en" to "An integrated Moroccan Islamic application for prayer times, Quran, and Adhkar",
            "fr" to "Une application islamique marocaine intégrée pour les prières, le Coran et l'Adhkar"
        ),
        "qibla" to mapOf(
            "ar" to "بوصلة اتجاه القبلة",
            "en" to "Qibla Compass",
            "fr" to "Boussole de la Qibla"
        ),
        "qibla_desc" to mapOf(
            "ar" to "تحديد اتجاه الكعبة المشرفة بدقة",
            "en" to "Accurately locate the direction of Kaaba",
            "fr" to "Localiser précisément la direction de la Kaaba"
        ),
        "tasbih" to mapOf(
            "ar" to "المسبحة الإلكترونية",
            "en" to "Electronic Tasbih",
            "fr" to "Chapelet Électronique"
        ),
        "tasbih_desc" to mapOf(
            "ar" to "عداد التسبيح اليومي والأذكار والسنن",
            "en" to "Daily dhikr and tasbih counter",
            "fr" to "Compteur quotidien de dhikr et tasbih"
        ),
        "share" to mapOf(
            "ar" to "مشاركة التطبيق مع الأصحاب والخير",
            "en" to "Share app with friends",
            "fr" to "Partager l'application"
        ),
        "share_sub" to mapOf(
            "ar" to "الدال على الخير كفاعله",
            "en" to "Reward sharing the goodness",
            "fr" to "Partager pour faire le bien"
        ),
        "share_text" to mapOf(
            "ar" to "✨ حمل تطبيق نور الإيمان لمعرفة مواعيد الصلاة وقراءة القرآن الكريم والأذكار اليومية في المغرب! ✨",
            "en" to "✨ Download Noor Al-Iman App for prayer times, Holy Quran, and daily Adhkar! ✨",
            "fr" to "✨ Téléchargez l'application Noor Al-Iman pour les prières, le Coran et l'Adhkar ! ✨"
        ),
        "share_chooser" to mapOf(
            "ar" to "مشاركة نور الإيمان",
            "en" to "Share Noor Al-Iman",
            "fr" to "Partager Noor Al-Iman"
        ),
        "about_item" to mapOf(
            "ar" to "حول تطبيق نور الإيمان",
            "en" to "About Noor Al-Iman",
            "fr" to "À propos du Noor Al-Iman"
        ),
        "about_item_sub" to mapOf(
            "ar" to "معلومات الإصدار وتأصيل الأوقات الشرعية",
            "en" to "Version information and official prayer sources",
            "fr" to "Informations de version et méthodologie"
        ),
        "about_body1" to mapOf(
            "ar" to "برمجة هذا التطبيق لخدمة المسلمين في المملكة المغربية الشريفة وعموم بلدان العالم الإسلامي.",
            "en" to "Developed to serve Muslims in the Kingdom of Morocco and around the Islamic world.",
            "fr" to "Développé pour les musulmans au Royaume du Maroc et dans le monde islamique."
        ),
        "about_body2" to mapOf(
            "ar" to "تم حساب أوقات الصلوات وفقاً للمنهجية الشرعية للوزارة الموقرة للأوقاف والشؤون الإسلامية بالمغرب، للتأكد من المواعيد الرسمية للمملكة.",
            "en" to "Prayer times are computed according to the official religious guidelines of the Ministry of Awqaf and Islamic Affairs in Morocco.",
            "fr" to "Les heures de prière sont calculées selon la méthodologie du Ministère des Habous et des Affaires Islamiques du Maroc."
        ),
        "about_quote" to mapOf(
            "ar" to "«وَمَنْ أَحْسَنُ قَوْلًا مِّمَّن دَعَا إِلَى اللَّهِ وَعَمِلَ صَالِحًا وَقَالَ إِنَّنِي مِنَ الْمُسْلِمِينَ»",
            "en" to "“And who is better in speech than one who invites to Allah and does righteousness and says, 'Indeed, I am of the Muslims'”",
            "fr" to "« Et qui profère plus belles paroles que celui qui appelle à Allah, fait le bien et dit : Je suis du nombre des Musulmans »"
        ),
        "close_about" to mapOf(
            "ar" to "شكراً وموافق",
            "en" to "Close & Thank You",
            "fr" to "Fermer & Merci"
        ),
        "ayah_day" to mapOf(
            "ar" to "آية اليوم",
            "en" to "Ayah of the Day",
            "fr" to "Verset du Jour"
        ),
        "explore_ayah" to mapOf(
            "ar" to "تفكر في آيات الله الكريمة",
            "en" to "Contemplate the verses of Allah",
            "fr" to "Méditer sur les versets de Dieu"
        ),
        "search_hint" to mapOf(
            "ar" to "ابحث عن سورة، آية، أو كلمة بالقرآن الكريم...",
            "en" to "Search for surah, verse, or word in Quran...",
            "fr" to "Rechercher une sourate, un verset ou mot..."
        ),
        "surah_label" to mapOf(
            "ar" to "سورة",
            "en" to "Surah",
            "fr" to "Sourate"
        ),
        "ayah_label" to mapOf(
            "ar" to "الآية",
            "en" to "Verse",
            "fr" to "Verset"
        ),
        "page_label" to mapOf(
            "ar" to "صفحة",
            "en" to "Page",
            "fr" to "Page"
        ),
        "search_results" to mapOf(
            "ar" to "تم العثور على %d آية تطابق بحثك",
            "en" to "Found %d verses matching your search",
            "fr" to "Trouvé %d versets correspondant"
        ),
        "fajr" to mapOf(
            "ar" to "الفجر",
            "en" to "Fajr",
            "fr" to "Fajr"
        ),
        "shorooq" to mapOf(
            "ar" to "الشروق",
            "en" to "Sunrise",
            "fr" to "Lever du soleil"
        ),
        "dhuhr" to mapOf(
            "ar" to "الظهر",
            "en" to "Dhuhr",
            "fr" to "Dhuhr"
        ),
        "asr" to mapOf(
            "ar" to "العصر",
            "en" to "Asr",
            "fr" to "Asr"
        ),
        "maghrib" to mapOf(
            "ar" to "المغرب",
            "en" to "Maghrib",
            "fr" to "Maghrib"
        ),
        "isha" to mapOf(
            "ar" to "العشاء",
            "en" to "Isha",
            "fr" to "Isha"
        ),
        "next_prayer" to mapOf(
            "ar" to "الصلاة القادمة",
            "en" to "Next Prayer",
            "fr" to "Prochaine Prière"
        ),
        "remaining_time" to mapOf(
            "ar" to "الوقت المتبقي للأذان",
            "en" to "Time remaining for Adhan",
            "fr" to "Temps restant avant l'Adhan"
        ),
        "services_title" to mapOf(
            "ar" to "الخدمات المتاحة",
            "en" to "Available Services",
            "fr" to "Services Disponibles"
        ),
        "general_options" to mapOf(
            "ar" to "خيارات عامة",
            "en" to "General Options",
            "fr" to "Options Générales"
        ),
        "adhkar_categories" to mapOf(
            "ar" to "تصنيفات الأذكار",
            "en" to "Adhkar Categories",
            "fr" to "Catégories d'Adhkar"
        ),
        "tasbih_count" to mapOf(
            "ar" to "العداد",
            "en" to "Counter",
            "fr" to "Compteur"
        ),
        "reset" to mapOf(
            "ar" to "إعادة تعيين",
            "en" to "Reset",
            "fr" to "Réinitialiser"
        ),
        "change_language" to mapOf(
            "ar" to "مسؤولي اللغة • Language Setting",
            "en" to "App Language",
            "fr" to "Langue de l'application"
        ),
        "language_section" to mapOf(
            "ar" to "لغة التطبيق • Langues",
            "en" to "Select Application Language",
            "fr" to "Choisir la Langue"
        ),
        "save" to mapOf(
            "ar" to "حفظ",
            "en" to "Save",
            "fr" to "Enregistrer"
        ),
        "index" to mapOf(
            "ar" to "الفهرس",
            "en" to "Index",
            "fr" to "Index"
        ),
        "search_in_quran" to mapOf(
            "ar" to "البحث في القرآن",
            "en" to "Search in Quran",
            "fr" to "Recherche dans le Coran"
        ),
        "quick_search_suggestions" to mapOf(
            "ar" to "اقتراحات البحث السريع:",
            "en" to "Quick search suggestions:",
            "fr" to "Suggestions de recherche rapide :"
        ),
        "no_results_for" to mapOf(
            "ar" to "لم يتم العثور على نتائج لـ",
            "en" to "No results found for",
            "fr" to "Aucun résultat trouvé pour"
        ),
        "matching_surahs" to mapOf(
            "ar" to "السور المطابقة",
            "en" to "Matching Surahs",
            "fr" to "Sourates correspondantes"
        ),
        "matching_verses" to mapOf(
            "ar" to "الآيات المطابقة",
            "en" to "Matching Verses",
            "fr" to "Versets correspondants"
        ),
        "start_page" to mapOf(
            "ar" to "بداية الصفحة",
            "en" to "Starts at page",
            "fr" to "Commence à la page"
        ),
        "translation_en" to mapOf(
            "ar" to "الترجمة (الإنجليزية)",
            "en" to "Translation (English)",
            "fr" to "Traduction (Anglaise)"
        ),
        "translation_fr" to mapOf(
            "ar" to "الترجمة (الفرنسية)",
            "en" to "Translation (French)",
            "fr" to "Traduction (Française)"
        ),
        "tafsir_muyassar" to mapOf(
            "ar" to "التفسير الميسر",
            "en" to "Al-Moyassar Tafsir",
            "fr" to "Tafsir Al-Moyassar"
        ),
        "play_surah" to mapOf(
            "ar" to "تشغيل السورة",
            "en" to "Play Surah",
            "fr" to "Jouer la sourate"
        ),
        "stop_surah" to mapOf(
            "ar" to "إيقاف السورة",
            "en" to "Stop Surah",
            "fr" to "Arrêter la sourate"
        ),
        "adhkar_fortifications" to mapOf(
            "ar" to "الأذكار والتحصينات",
            "en" to "Adhkar & Fortifications",
            "fr" to "Adhkar & Invocations"
        ),
        "morning_adhkar" to mapOf(
            "ar" to "أذكار الصباح",
            "en" to "Morning Adhkar",
            "fr" to "Adhkar du Matin"
        ),
        "evening_adhkar" to mapOf(
            "ar" to "أذكار المساء",
            "en" to "Evening Adhkar",
            "fr" to "Adhkar du Soir"
        ),
        "after_prayer_adhkar" to mapOf(
            "ar" to "أذكار بعد الصلاة",
            "en" to "After Prayer Adhkar",
            "fr" to "Adhkar après la Prière"
        ),
        "sleep_adhkar" to mapOf(
            "ar" to "أذكار النوم",
            "en" to "Sleep Adhkar",
            "fr" to "Adhkar du Sommeil"
        ),
        "waking_up_adhkar" to mapOf(
            "ar" to "أذكار الاستيقاظ",
            "en" to "Waking Up Adhkar",
            "fr" to "Adhkar du Réveil"
        ),
        "travel_adhkar" to mapOf(
            "ar" to "أذكار السفر",
            "en" to "Travel Adhkar",
            "fr" to "Adhkar de Voyage"
        ),
        "sorrow_adhkar" to mapOf(
            "ar" to "أذكار الهم والحزن",
            "en" to "Sorrow & Anxiety Adhkar",
            "fr" to "Adhkar d'Anxiété et Tristesse"
        ),
        "of_adhkar" to mapOf(
            "ar" to "من الأذكار",
            "en" to "Adhkar",
            "fr" to "Adhkar"
        ),
        "completed" to mapOf(
            "ar" to "مكتمل",
            "en" to "Completed",
            "fr" to "Terminé"
        ),
        "click_card_to_repeat" to mapOf(
            "ar" to "انقر فوق البطاقة للتكرار",
            "en" to "Tap card to repeat",
            "fr" to "Appuyez pour répéter"
        ),
        "qibla_calibrating" to mapOf(
            "ar" to "جاري تحديد الموقع و اتجاه الكعبة...",
            "en" to "Locating Qibla...",
            "fr" to "Localisation de la Qibla..."
        ),
        "qibla_calibration_hint" to mapOf(
            "ar" to "ضع الهاتف مستوياً للحصول على دقة أفضل",
            "en" to "Keep phone flat for best accuracy",
            "fr" to "Gardez le téléphone à plat pour plus de précision"
        ),
        "qibla_direction_degrees" to mapOf(
            "ar" to "اتجاه القبلة:",
            "en" to "Qibla Direction:",
            "fr" to "Direction de la Qibla :"
        ),
        "degrees_from_north" to mapOf(
            "ar" to "درجة من الشمال",
            "en" to "degrees from North",
            "fr" to "degrés du Nord"
        ),
        "qibla_ready" to mapOf(
            "ar" to "جاهز للصلاة! الهاتف مطابق لاتجاه القبلة",
            "en" to "Ready for prayer! Phone aligned with Qibla",
            "fr" to "Prêt pour la prière ! Téléphone aligné vers la Qibla"
        ),
        "qibla_rotate_hint" to mapOf(
            "ar" to "يرجى تدوير الهاتف حتى يتطابق المؤشر مع القبلة",
            "en" to "Please rotate phone to align with Qibla indicator",
            "fr" to "Veuillez tourner le téléphone pour l'aligner avec la Qibla"
        ),
        "current_dhikr" to mapOf(
            "ar" to "الذكر الحالي",
            "en" to "Current Dhikr",
            "fr" to "Dhikr Actuel"
        ),
        "cycle_limit" to mapOf(
            "ar" to "دورة",
            "en" to "Cycle",
            "fr" to "Cycle"
        ),
        "total_tasbih_today" to mapOf(
            "ar" to "مجموع التسبيحات اليوم",
            "en" to "Total Tasbih Today",
            "fr" to "Total Tasbih Aujourd'hui"
        )
    )

    fun translate(key: String, language: AppLanguage): String {
        return dictionary[key]?.get(language.code) ?: dictionary[key]?.get("ar") ?: key
    }
}
