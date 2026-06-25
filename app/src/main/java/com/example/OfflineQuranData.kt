package com.example

object OfflineQuranData {
    val JSON_DATA = """
    {
      "code": 200,
      "status": "OK",
      "data": {
        "surahs": [
          {
            "number": 1,
            "name": "سُورَةُ الْفَاتِحَةِ",
            "englishName": "Al-Fatiha",
            "englishNameTranslation": "The Opening",
            "revelationType": "Meccan",
            "ayahs": [
              { "number": 1, "text": "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "numberInSurah": 1, "juz": 1, "manzil": 1, "page": 1, "ruku": 1, "hizbQuarter": 1, "sajda": false },
              { "number": 2, "text": "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "numberInSurah": 2, "juz": 1, "manzil": 1, "page": 1, "ruku": 1, "hizbQuarter": 1, "sajda": false },
              { "number": 3, "text": "الرَّحْمَٰنِ الرَّحِيمِ", "numberInSurah": 3, "juz": 1, "manzil": 1, "page": 1, "ruku": 1, "hizbQuarter": 1, "sajda": false },
              { "number": 4, "text": "مَالِكِ يَوْمِ الدِّينِ", "numberInSurah": 4, "juz": 1, "manzil": 1, "page": 1, "ruku": 1, "hizbQuarter": 1, "sajda": false },
              { "number": 5, "text": "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "numberInSurah": 5, "juz": 1, "manzil": 1, "page": 1, "ruku": 1, "hizbQuarter": 1, "sajda": false },
              { "number": 6, "text": "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "numberInSurah": 6, "juz": 1, "manzil": 1, "page": 1, "ruku": 1, "hizbQuarter": 1, "sajda": false },
              { "number": 7, "text": "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "numberInSurah": 7, "juz": 1, "manzil": 1, "page": 1, "ruku": 1, "hizbQuarter": 1, "sajda": false }
            ]
          },
          {
            "number": 112,
            "name": "سُورَةُ الإِخْلَاصِ",
            "englishName": "Al-Ikhlaas",
            "englishNameTranslation": "Sincerity",
            "revelationType": "Meccan",
            "ayahs": [
              { "number": 6222, "text": "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ قُلْ هُوَ اللَّهُ أَحَدٌ", "numberInSurah": 1, "juz": 30, "manzil": 7, "page": 604, "ruku": 554, "hizbQuarter": 240, "sajda": false },
              { "number": 6223, "text": "اللَّهُ الصَّمَدُ", "numberInSurah": 2, "juz": 30, "manzil": 7, "page": 604, "ruku": 554, "hizbQuarter": 240, "sajda": false },
              { "number": 6224, "text": "لَمْ يَلِدْ وَلَمْ يُولَدْ", "numberInSurah": 3, "juz": 30, "manzil": 7, "page": 604, "ruku": 554, "hizbQuarter": 240, "sajda": false },
              { "number": 6225, "text": "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "numberInSurah": 4, "juz": 30, "manzil": 7, "page": 604, "ruku": 554, "hizbQuarter": 240, "sajda": false }
            ]
          },
          {
            "number": 113,
            "name": "سُورَةُ الْفَلَقِ",
            "englishName": "Al-Falaq",
            "englishNameTranslation": "The Daybreak",
            "revelationType": "Meccan",
            "ayahs": [
              { "number": 6226, "text": "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "numberInSurah": 1, "juz": 30, "manzil": 7, "page": 604, "ruku": 555, "hizbQuarter": 240, "sajda": false },
              { "number": 6227, "text": "مِن شَرِّ مَا خَلَقَ", "numberInSurah": 2, "juz": 30, "manzil": 7, "page": 604, "ruku": 555, "hizbQuarter": 240, "sajda": false },
              { "number": 6228, "text": "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "numberInSurah": 3, "juz": 30, "manzil": 7, "page": 604, "ruku": 555, "hizbQuarter": 240, "sajda": false },
              { "number": 6229, "text": "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "numberInSurah": 4, "juz": 30, "manzil": 7, "page": 604, "ruku": 555, "hizbQuarter": 240, "sajda": false },
              { "number": 6230, "text": "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "numberInSurah": 5, "juz": 30, "manzil": 7, "page": 604, "ruku": 555, "hizbQuarter": 240, "sajda": false }
            ]
          },
          {
            "number": 114,
            "name": "سُورَةُ النَّاسِ",
            "englishName": "An-Naas",
            "englishNameTranslation": "Mankind",
            "revelationType": "Meccan",
            "ayahs": [
              { "number": 6231, "text": "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "numberInSurah": 1, "juz": 30, "manzil": 7, "page": 604, "ruku": 556, "hizbQuarter": 240, "sajda": false },
              { "number": 6232, "text": "مَلِكِ النَّاسِ", "numberInSurah": 2, "juz": 30, "manzil": 7, "page": 604, "ruku": 556, "hizbQuarter": 240, "sajda": false },
              { "number": 6233, "text": "إِلَٰهِ النَّاسِ", "numberInSurah": 3, "juz": 30, "manzil": 7, "page": 604, "ruku": 556, "hizbQuarter": 240, "sajda": false },
              { "number": 6234, "text": "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "numberInSurah": 4, "juz": 30, "manzil": 7, "page": 604, "ruku": 556, "hizbQuarter": 240, "sajda": false },
              { "number": 6235, "text": "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "numberInSurah": 5, "juz": 30, "manzil": 7, "page": 604, "ruku": 556, "hizbQuarter": 240, "sajda": false },
              { "number": 6236, "text": "مِنَ الْجِنَّةِ وَالنَّاسِ", "numberInSurah": 6, "juz": 30, "manzil": 7, "page": 604, "ruku": 556, "hizbQuarter": 240, "sajda": false }
            ]
          }
        ]
      }
    }
    """.trimIndent()

    val OFFLINE_TRANSLATION_FR = mapOf(
        1 to "Au nom d'Allah, le Tout Miséricordieux, le Très Miséricordieux.",
        2 to "Louange à Allah, Seigneur de l'univers.",
        3 to "Le Tout Miséricordieux, le Très Miséricordieux,",
        4 to "Maître du Jour de la rétribution.",
        5 to "C'est Toi [Seul] que nous adorons, et c'est Toi [Seul] dont nous implorons l'assistance.",
        6 to "Guide-nous dans le droit chemin,",
        7 to "le chemin de ceux que Tu as comblés de faveurs, non pas de ceux qui ont encouru Ta colère, ni des égarés."
    )

    val OFFLINE_TRANSLATION_EN = mapOf(
        1 to "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
        2 to "[All] praise is [due] to Allah, Lord of the worlds -",
        3 to "The Entirely Merciful, the Especially Merciful,",
        4 to "Sovereign of the Day of Recompense.",
        5 to "It is You we worship and You we ask for help.",
        6 to "Guide us to the straight path -",
        7 to "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray."
    )

    val OFFLINE_TAFSIR_AR = mapOf(
        1 to "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ: أبدأ قراءتي مستعينا بالله تبارك وتعالى، الرحمن الذي وسعت رحمته كل شيء، الرحيم بعباده المؤمنين.",
        2 to "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ: الثناء الكامل والحمد المطلق لله وحده، خالق الخلق ومالكهم ومربيهم بنعمه.",
        3 to "الرَّحْمَٰنِ الرَّحِيمِ: ذو الرحمة الواسعة التي شملت جميع الخلائق، والرحمة الخاصة بالمؤمنين.",
        4 to "مَالِكِ يَوْمِ الدِّينِ: وهو وحده سبحانه المالك الحقيقي ليوم الحساب والجزاء وهو يوم القيامة.",
        5 to "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ: نخصك وحدك بالعبادة والانقياد، ونستعين بك وحدك في جميع أمورنا.",
        6 to "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ: دلّنا وأرشدنا وثبّتنا على الطريق الواضح الموصل إليك وإلى جنتك، وهو الإسلام.",
        7 to "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ: طريق الذين أنعمت عليهم من النبيين والصديقين والشهداء والصالحين، غير طريق المغضوب عليهم الذين عرفوا الحق ولم يعملوا به كاليهود، وغير الضالين الذين ضلوا عن الحق كالندامى والنصارى."
    )
}
