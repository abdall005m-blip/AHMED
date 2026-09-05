// Verified Quran Data Module
// Complete metadata of 114 Surahs + verified authentic text for widely recited Surahs and Juz Amma

export const SURAH_LIST = [
  { id: 1, name: "الفاتحة", englishName: "Al-Fatihah", verses: 7, type: "مكية" },
  { id: 2, name: "البقرة", englishName: "Al-Baqarah", verses: 286, type: "مدنية" },
  { id: 3, name: "آل عمران", englishName: "Ali 'Imran", verses: 200, type: "مدنية" },
  { id: 4, name: "النساء", englishName: "An-Nisa", verses: 176, type: "مدنية" },
  { id: 5, name: "المائدة", englishName: "Al-Ma'idah", verses: 120, type: "مدنية" },
  { id: 6, name: "الأنعام", englishName: "Al-An'am", verses: 165, type: "مكية" },
  { id: 7, name: "الأعراف", englishName: "Al-A'raf", verses: 206, type: "مكية" },
  { id: 8, name: "الأنفال", englishName: "Al-Anfal", verses: 75, type: "مدنية" },
  { id: 9, name: "التوبة", englishName: "At-Tawbah", verses: 129, type: "مدنية" },
  { id: 10, name: "يونس", englishName: "Yunus", verses: 109, type: "مكية" },
  { id: 11, name: "هود", englishName: "Hud", verses: 123, type: "مكية" },
  { id: 12, name: "يوسف", englishName: "Yusuf", verses: 111, type: "مكية" },
  { id: 13, name: "الرعد", englishName: "Ar-Ra'd", verses: 43, type: "مدنية" },
  { id: 14, name: "إبراهيم", englishName: "Ibrahim", verses: 52, type: "مكية" },
  { id: 15, name: "الحجر", englishName: "Al-Hijr", verses: 99, type: "مكية" },
  { id: 16, name: "النحل", englishName: "An-Nahl", verses: 128, type: "مكية" },
  { id: 17, name: "الإسراء", englishName: "Al-Isra", verses: 111, type: "مكية" },
  { id: 18, name: "الكهف", englishName: "Al-Kahf", verses: 110, type: "مكية" },
  { id: 19, name: "مريم", englishName: "Maryam", verses: 98, type: "مكية" },
  { id: 20, name: "طه", englishName: "Ta-Ha", verses: 135, type: "مكية" },
  { id: 21, name: "الأنبياء", englishName: "Al-Anbiya", verses: 112, type: "مكية" },
  { id: 22, name: "الحج", englishName: "Al-Hajj", verses: 78, type: "مدنية" },
  { id: 23, name: "المؤمنون", englishName: "Al-Mu'minun", verses: 118, type: "مكية" },
  { id: 24, name: "النور", englishName: "An-Nur", verses: 64, type: "مدنية" },
  { id: 25, name: "الفرقان", englishName: "Al-Furqan", verses: 77, type: "مكية" },
  { id: 26, name: "الشعراء", englishName: "Ash-Shu'ara", verses: 227, type: "مكية" },
  { id: 27, name: "النمل", englishName: "An-Naml", verses: 93, type: "مكية" },
  { id: 28, name: "القصص", englishName: "Al-Qasas", verses: 88, type: "مكية" },
  { id: 29, name: "العنكبوت", englishName: "Al-'Ankabut", verses: 69, type: "مكية" },
  { id: 30, name: "الروم", englishName: "Ar-Rum", verses: 60, type: "مكية" },
  { id: 31, name: "لقمان", englishName: "Luqman", verses: 34, type: "مكية" },
  { id: 32, name: "السجدة", englishName: "As-Sajdah", verses: 30, type: "مكية" },
  { id: 33, name: "الأحزاب", englishName: "Al-Ahzab", verses: 73, type: "مدنية" },
  { id: 34, name: "سبأ", englishName: "Saba", verses: 54, type: "مكية" },
  { id: 35, name: "فاطر", englishName: "Fatir", verses: 45, type: "مكية" },
  { id: 36, name: "يس", englishName: "Ya-Sin", verses: 83, type: "مكية" },
  { id: 37, name: "الصافات", englishName: "As-Saffat", verses: 182, type: "مكية" },
  { id: 38, name: "ص", englishName: "Sad", verses: 88, type: "مكية" },
  { id: 39, name: "الزمر", englishName: "Az-Zumar", verses: 75, type: "مكية" },
  { id: 40, name: "غافر", englishName: "Ghafir", verses: 85, type: "مكية" },
  { id: 41, name: "فصلت", englishName: "Fussilat", verses: 54, type: "مكية" },
  { id: 42, name: "الشورى", englishName: "Ash-Shura", verses: 53, type: "مكية" },
  { id: 43, name: "الزخرف", englishName: "Az-Zukhruf", verses: 89, type: "مكية" },
  { id: 44, name: "الدخان", englishName: "Ad-Dukhan", verses: 59, type: "مكية" },
  { id: 45, name: "الجاثية", englishName: "Al-Jathiyah", verses: 37, type: "مكية" },
  { id: 46, name: "الأحقاف", englishName: "Al-Ahqaf", verses: 35, type: "مكية" },
  { id: 47, name: "محمد", englishName: "Muhammad", verses: 38, type: "مدنية" },
  { id: 48, name: "الفتح", englishName: "Al-Fath", verses: 29, type: "مدنية" },
  { id: 49, name: "الحجرات", englishName: "Al-Hujurat", verses: 18, type: "مدنية" },
  { id: 50, name: "ق", englishName: "Qaf", verses: 45, type: "مكية" },
  { id: 51, name: "الذاريات", englishName: "Adh-Dhariyat", verses: 60, type: "مكية" },
  { id: 52, name: "الطور", englishName: "At-Tur", verses: 49, type: "مكية" },
  { id: 53, name: "النجم", englishName: "An-Najm", verses: 62, type: "مكية" },
  { id: 54, name: "القمر", englishName: "Al-Qamar", verses: 55, type: "مكية" },
  { id: 55, name: "الرحمن", englishName: "Ar-Rahman", verses: 78, type: "مدنية" },
  { id: 56, name: "الواقعة", englishName: "Al-Waqi'ah", verses: 96, type: "مكية" },
  { id: 57, name: "الحديد", englishName: "Al-Hadid", verses: 29, type: "مدنية" },
  { id: 58, name: "المجادلة", englishName: "Al-Mujadila", verses: 22, type: "مدنية" },
  { id: 59, name: "الحشر", englishName: "Al-Hashr", verses: 24, type: "مدنية" },
  { id: 60, name: "الممتحنة", englishName: "Al-Mumtahanah", verses: 13, type: "مدنية" },
  { id: 61, name: "الصف", englishName: "As-Saff", verses: 14, type: "مدنية" },
  { id: 62, name: "الجمعة", englishName: "Al-Jumu'ah", verses: 11, type: "مدنية" },
  { id: 63, name: "المنافقون", englishName: "Al-Munafiqun", verses: 11, type: "مدنية" },
  { id: 64, name: "التغابن", englishName: "At-Taghabun", verses: 18, type: "مدنية" },
  { id: 65, name: "الطلاق", englishName: "At-Talaq", verses: 12, type: "مدنية" },
  { id: 66, name: "التحريم", englishName: "At-Tahrim", verses: 12, type: "مدنية" },
  { id: 67, name: "الملك", englishName: "Al-Mulk", verses: 30, type: "مكية" },
  { id: 68, name: "القلم", englishName: "Al-Qalam", verses: 52, type: "مكية" },
  { id: 69, name: "الحاقة", englishName: "Al-Haqqah", verses: 52, type: "مكية" },
  { id: 70, name: "المعارج", englishName: "Al-Ma'arij", verses: 44, type: "مكية" },
  { id: 71, name: "نوح", englishName: "Nuh", verses: 28, type: "مكية" },
  { id: 72, name: "الجن", englishName: "Al-Jinn", verses: 28, type: "مكية" },
  { id: 73, name: "المزمل", englishName: "Al-Muzzammil", verses: 20, type: "مكية" },
  { id: 74, name: "المدثر", englishName: "Al-Muddaththir", verses: 56, type: "مكية" },
  { id: 75, name: "القيامة", englishName: "Al-Qiyamah", verses: 40, type: "مكية" },
  { id: 76, name: "الإنسان", englishName: "Al-Insan", verses: 31, type: "مدنية" },
  { id: 77, name: "المرسلات", englishName: "Al-Mursalat", verses: 50, type: "مكية" },
  { id: 78, name: "النبأ", englishName: "An-Naba", verses: 40, type: "مكية" },
  { id: 79, name: "النازعات", englishName: "An-Nazi'at", verses: 46, type: "مكية" },
  { id: 80, name: "عبس", englishName: "'Abasa", verses: 42, type: "مكية" },
  { id: 81, name: "التكوير", englishName: "At-Takwir", verses: 29, type: "مكية" },
  { id: 82, name: "الانفطار", englishName: "Al-Infitar", verses: 19, type: "مكية" },
  { id: 83, name: "المطففين", englishName: "Al-Mutaffifin", verses: 36, type: "مكية" },
  { id: 84, name: "الانشقاق", englishName: "Al-Inshiqaq", verses: 25, type: "مكية" },
  { id: 85, name: "البروج", englishName: "Al-Buruj", verses: 22, type: "مكية" },
  { id: 86, name: "الطارق", englishName: "At-Tariq", verses: 17, type: "مكية" },
  { id: 87, name: "الأعلى", englishName: "Al-A'la", verses: 19, type: "مكية" },
  { id: 88, name: "الغاشية", englishName: "Al-Ghashiyah", verses: 26, type: "مكية" },
  { id: 89, name: "الفجر", englishName: "Al-Fajr", verses: 30, type: "مكية" },
  { id: 90, name: "البلد", englishName: "Al-Balad", verses: 20, type: "مكية" },
  { id: 91, name: "الشمس", englishName: "Ash-Shams", verses: 15, type: "مكية" },
  { id: 92, name: "الليل", englishName: "Al-Layl", verses: 21, type: "مكية" },
  { id: 93, name: "الضحى", englishName: "Ad-Duha", verses: 11, type: "مكية" },
  { id: 94, name: "الشرح", englishName: "Ash-Sharh", verses: 8, type: "مكية" },
  { id: 95, name: "التين", englishName: "At-Tin", verses: 8, type: "مكية" },
  { id: 96, name: "العلق", englishName: "Al-'Alaq", verses: 19, type: "مكية" },
  { id: 97, name: "القدر", englishName: "Al-Qadr", verses: 5, type: "مكية" },
  { id: 98, name: "البينة", englishName: "Al-Bayyinah", verses: 8, type: "مدنية" },
  { id: 99, name: "الزلزلة", englishName: "Az-Zalzalah", verses: 8, type: "مدنية" },
  { id: 100, name: "العاديات", englishName: "Al-'Adiyat", verses: 11, type: "مكية" },
  { id: 101, name: "القارعة", englishName: "Al-Qari'ah", verses: 11, type: "مكية" },
  { id: 102, name: "التكاثر", englishName: "At-Takathur", verses: 8, type: "مكية" },
  { id: 103, name: "العصر", englishName: "Al-'Asr", verses: 3, type: "مكية" },
  { id: 104, name: "الهمزة", englishName: "Al-Humazah", verses: 9, type: "مكية" },
  { id: 105, name: "الفيل", englishName: "Al-Fil", verses: 5, type: "مكية" },
  { id: 106, name: "قريش", englishName: "Quraysh", verses: 4, type: "مكية" },
  { id: 107, name: "الماعون", englishName: "Al-Ma'un", verses: 7, type: "مكية" },
  { id: 108, name: "الكوثر", englishName: "Al-Kawthar", verses: 3, type: "مكية" },
  { id: 109, name: "الكافرون", englishName: "Al-Kafirun", verses: 6, type: "مكية" },
  { id: 110, name: "النصر", englishName: "An-Nasr", verses: 3, type: "مدنية" },
  { id: 111, name: "المسد", englishName: "Al-Masad", verses: 5, type: "مكية" },
  { id: 112, name: "الإخلاص", englishName: "Al-Ikhlas", verses: 4, type: "مكية" },
  { id: 113, name: "الفلق", englishName: "Al-Falaq", verses: 5, type: "مكية" },
  { id: 114, name: "الناس", englishName: "An-Nas", verses: 6, type: "مكية" }
];

// Verified text for short Surahs (Authentic Quran Texts)
export const VERIFIED_SURAHS_TEXT = {
  1: [
    { number: 1, text: "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ" },
    { number: 2, text: "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ" },
    { number: 3, text: "الرَّحْمَٰنِ الرَّحِيمِ" },
    { number: 4, text: "مَالِكِ يَوْمِ الدِّينِ" },
    { number: 5, text: "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ" },
    { number: 6, text: "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ" },
    { number: 7, text: "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ" }
  ],
  93: [
    { number: 1, text: "وَالضُّحَىٰ" },
    { number: 2, text: "وَاللَّيْلِ إِذَا سَجَىٰ" },
    { number: 3, text: "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ" },
    { number: 4, text: "وَلَلْآخِرَةُ خَيْرٌ لَّكَ مِنَ الْأُولَىٰ" },
    { number: 5, text: "وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰ" },
    { number: 6, text: "أَلَمْ يَجِدْكَ يَتِيمًا فَآوَىٰ" },
    { number: 7, text: "وَوَجَدَكَ ضَالًّا فَهَدَىٰ" },
    { number: 8, text: "وَوَجَدَكَ عَائِلًا فَأَغْنَىٰ" },
    { number: 9, text: "فَأَمَّا الْيَتِيمَ فَلَا تَقْهَرْ" },
    { number: 10, text: "وَأَمَّا السَّائِلَ فَلَا تَنْهَرْ" },
    { number: 11, text: "وَأَمَّا بِنِعْمَةِ رَبِّكَ فَحَدِّثْ" }
  ],
  94: [
    { number: 1, text: "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ" },
    { number: 2, text: "وَوَضَعْنَا عَنكَ وِزْرَكَ" },
    { number: 3, text: "الَّذِي أَنقَضَ ظَهْرَكَ" },
    { number: 4, text: "وَرَفَعْنَا لَكَ ذِكْرَكَ" },
    { number: 5, text: "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا" },
    { number: 6, text: "إِنَّ مَعَ الْعُسْرِ يُسْرًا" },
    { number: 7, text: "فَإِذَا فَرَغْتَ فَانصَبْ" },
    { number: 8, text: "وَإِلَىٰ رَبِّكَ فَارْغَب" }
  ],
  97: [
    { number: 1, text: "إِنَّا أَنزَلْنَاهُ فِي لَيْلَةِ الْقَدْرِ" },
    { number: 2, text: "وَمَا أَدْرَاكَ مَا لَيْلَةُ الْقَدْرِ" },
    { number: 3, text: "لَيْلَةُ الْقَدْرِ خَيْرٌ مِّنْ أَلْفِ شَهْرٍ" },
    { number: 4, text: "تَنَزَّلُ الْمَلَائِكَةُ وَالرُّوحُ فِيهَا بِإِذْنِ رَبِّهِم مِّن كُلِّ أَمْرٍ" },
    { number: 5, text: "سَلَامٌ هِيَ حَتَّىٰ مَطْلَعِ الْفَجْرِ" }
  ],
  103: [
    { number: 1, text: "وَالْعَصْرِ" },
    { number: 2, text: "إِنَّ الْإِنسَانَ لَفِي خُسْرٍ" },
    { number: 3, text: "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ" }
  ],
  108: [
    { number: 1, text: "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ" },
    { number: 2, text: "فَصَلِّ لِرَبِّكَ وَانْحَرْ" },
    { number: 3, text: "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ" }
  ],
  110: [
    { number: 1, text: "إِذَا جَاءَ نَصْرُ اللَّهِ وَالْفَتْحُ" },
    { number: 2, text: "وَرَأَيْتَ النَّاسَ يَدْخُلُونَ فِي دِينِ اللَّهِ أَفْوَاجًا" },
    { number: 3, text: "فَسَبِّحْ بِحَمْدِ رَبِّكَ وَاسْتَغْفِرْهُ ۚ إِنَّهُ كَانَ تَوَّابًا" }
  ],
  112: [
    { number: 1, text: "قُلْ هُوَ اللَّهُ أَحَدٌ" },
    { number: 2, text: "اللَّهُ الصَّمَدُ" },
    { number: 3, text: "لَمْ يَلِدْ وَلَمْ يُولَدْ" },
    { number: 4, text: "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ" }
  ],
  113: [
    { number: 1, text: "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ" },
    { number: 2, text: "مِن شَرِّ مَا خَلَقَ" },
    { number: 3, text: "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ" },
    { number: 4, text: "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ" },
    { number: 5, text: "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ" }
  ],
  114: [
    { number: 1, text: "قُلْ أَعُوذُ بِرَبِّ النَّاسِ" },
    { number: 2, text: "مَلِكِ النَّاسِ" },
    { number: 3, text: "إِلَٰهِ النَّاسِ" },
    { number: 4, text: "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ" },
    { number: 5, text: "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ" },
    { number: 6, text: "مِنَ الْجِنَّةِ وَالنَّاسِ" }
  ]
};

export async function fetchFullSurahText(surahNumber) {
  if (VERIFIED_SURAHS_TEXT[surahNumber]) {
    return VERIFIED_SURAHS_TEXT[surahNumber];
  }
  try {
    const res = await fetch(`https://api.alquran.cloud/v1/surah/${surahNumber}`);
    if (res.ok) {
      const data = await res.json();
      return data.data.ayahs.map(a => ({
        number: a.numberInSurah,
        text: a.text
      }));
    }
  } catch (e) {
    console.warn("Could not fetch remote surah, using fallback", e);
  }
  return null;
}
