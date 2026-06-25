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
        7 to "le chemin de ceux que Tu as comblés de faveurs, non pas de ceux qui ont encouru Ta colère, ni des égarés.",
        // Al-Ikhlas (112)
        6222 to "Dis: «Il est Allah, Unique.",
        6223 to "Allah, Le Seul à être imploré pour ce que nous désirons.",
        6224 to "Il n'a jamais engendré, n'a pas été engendré non plus.",
        6225 to "Et nul n'est égal à Lui».",
        // Al-Falaq (113)
        6226 to "Dis: «Je cherche protection auprès du Seigneur de l'aube naissante,",
        6227 to "contre le mal des êtres qu'Il a créés,",
        6228 to "contre le mal de l'obscurité quand elle s'approfondit,",
        6229 to "contre le mal de celles qui soufflent (les sorcières) sur les nœuds,",
        6230 to "et contre le mal de l'envieux quand il envie».",
        // An-Naas (114)
        6231 to "Dis: «Je cherche protection auprès du Seigneur des hommes,",
        6232 to "Le Souverain des hommes,",
        6233 to "Dieu des hommes,",
        6234 to "contre le mal du mauvais conseilleur, furtif,",
        6235 to "qui souffle le mal dans les poitrines des hommes,",
        6236 to "qu'il (le conseiller) soit un djinn, ou un être humain»."
    )

    val OFFLINE_TRANSLATION_EN = mapOf(
        1 to "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
        2 to "[All] praise is [due] to Allah, Lord of the worlds -",
        3 to "The Entirely Merciful, the Especially Merciful,",
        4 to "Sovereign of the Day of Recompense.",
        5 to "It is You we worship and You we ask for help.",
        6 to "Guide us to the straight path -",
        7 to "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.",
        // Al-Ikhlas (112)
        6222 to "Say, \"He is Allah, [who is] One,",
        6223 to "Allah, the Eternal Refuge.",
        6224 to "He neither begets nor is born,",
        6225 to "Nor is there to Him any equivalent.\"",
        // Al-Falaq (113)
        6226 to "Say, \"I seek refuge in the Lord of daybreak",
        6227 to "From the evil of that which He created",
        6228 to "And from the evil of darkness when it settles",
        6229 to "And from the evil of the blowers in knots",
        6230 to "And from the evil of an envier when he envies.\"",
        // An-Naas (114)
        6231 to "Say, \"I seek refuge in the Lord of mankind,",
        6232 to "The Sovereign of mankind.",
        6233 to "The God of mankind,",
        6234 to "From the evil of the retreating whisperer -",
        6235 to "Who whispers [evil] into the breasts of mankind -",
        6236 to "From among the jinn and mankind.\""
    )

    val OFFLINE_TAFSIR_AR = mapOf(
        1 to "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ: أبدأ قراءتي مستعينا بالله تبارك وتعالى، الرحمن الذي وسعت رحمته كل شيء، الرحيم بعباده المؤمنين.",
        2 to "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ: الثناء الكامل والحمد المطلق لله وحده، خالق الخلق ومالكهم ومربيهم بنعمه.",
        3 to "الرَّحْمَٰنِ الرَّحِيمِ: ذو الرحمة الواسعة التي شملت جميع الخلائق، والرحمة الخاصة بالمؤمنين.",
        4 to "مَالِكِ يَوْمِ الدِّينِ: وهو وحده سبحانه المالك الحقيقي ليوم الحساب والجزاء وهو يوم القيامة.",
        5 to "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ: نخصك وحدك بالعبادة والانقياد، ونستعين بك وحدك في جميع أمورنا.",
        6 to "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ: دلّنا وأرشدنا وثبّتنا على الطريق الواضح الموصل إليك وإلى جنتك، وهو الإسلام.",
        7 to "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ: طريق الذين أنعمت عليهم من النبيين والصديقين والشهداء والصالحين، غير طريق المغضوب عليهم الذين عرفوا الحق ولم يعملوا به كاليهود، وغير الضالين الذين ضلوا عن الحق كالندامى والنصارى.",
        // Al-Ikhlas (112)
        6222 to "قُلْ هُوَ اللَّهُ أَحَدٌ: أي قل -أيها الرسول- هو الله المنفرد بالألوهية.",
        6223 to "اللَّهُ الصَّمَدُ: الذي تصمد إليه الخلائق في حاجاتها.",
        6224 to "لَمْ يَلِدْ وَلَمْ يُولَدْ: ليس له ولد ولا والد.",
        6225 to "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ: ولم يكن له مثيل ولا شبيه.",
        // Al-Falaq (113)
        6226 to "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ: قل أعوذ وأعتصم برب الصبح.",
        6227 to "مِن شَرِّ مَا خَلَقَ: من شر جميع المخلوقات.",
        6228 to "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ: ومن شر ليل شديد الظلمة إذا دخل وتخلل.",
        6229 to "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ: ومن شر السواحر اللاتي ينفخن في العقد.",
        6230 to "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ: ومن شر حاسد إذا حسد الناس على ما آتاهم الله.",
        // An-Naas (114)
        6231 to "قُلْ أَعُوذُ بِرَبِّ النَّاسِ: قل أعوذ وأعتصم برب الناس.",
        6232 to "مَلِكِ النَّاسِ: ملك الناس المتصرف في شؤونهم.",
        6233 to "إِلَٰهِ النَّاسِ: إله الناس الذي لا معبود بحق سواه.",
        6234 to "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ: من شر الشيطان الذي يوسوس عند الغفلة ويختفي عند الذكر.",
        6235 to "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ: الذي يبث الشر والشكوك في قلوب الناس.",
        6236 to "مِنَ الْجِنَّةِ وَالنَّاسِ: سواء كان الموسوس من الجن أو من الإنس."
    )
}
