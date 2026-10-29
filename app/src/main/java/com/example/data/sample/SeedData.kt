package com.example.data.sample

import com.example.data.model.*

/**
 * Official Master Seed Data for "LIFE SKILLS CAMPING 2026 / লাইফ স্কিলস ক্যাম্পিং ২০২৬"
 * Theme: "নিজকে গড়ো, গড়তে শেখাও" (Build Thyself, Help to Build)
 * Organizer: WAMY Bangladesh (Orphan Sponsorship Project)
 * Venue: Rangamati Waterfront Resort, Shafipur, Kaliakoir, Gazipur
 * Dates: 22–26 October 2026 (5 Days) | Total Orphan Students: 609
 */
object SeedData {

    val groups = listOf(
        CampGroupEntity(
            id = "bukhari",
            nameBn = "বুখারী গ্রুপ",
            nameEn = "Bukhari Group",
            leaderName = "আয়াতুল্লাহ নাহিদ",
            leaderPhone = "01711-260101",
            subgroupMentors = "Bukhari-01: হাফেজ ওয়ালী উল্লাহ, Bukhari-02: মো. শফিকুল ইসলাম, Bukhari-03: হাফেজ সাখাওয়াত হোসাইন, Bukhari-04: হাফেজ মো. আবিদ হাসান",
            colorHex = 0xFF1976D2,
            memberCount = 120,
            totalPoints = 940
        ),
        CampGroupEntity(
            id = "shafeyi",
            nameBn = "শাফে’য়ী গ্রুপ",
            nameEn = "Shafi'ee Group",
            leaderName = "সুলতান মাহমুদ",
            leaderPhone = "01711-260102",
            subgroupMentors = "Shafi'ee-01: হাফেজ সালাহউদ্দিন, Shafi'ee-02: হাফেজ মো. তানভীর আহমেদ, Shafi'ee-03: মো. আরিফুল ইসলাম, Shafi'ee-04: হাফেজ মো. মহসিন",
            colorHex = 0xFF00796B,
            memberCount = 123,
            totalPoints = 925
        ),
        CampGroupEntity(
            id = "salahuddin",
            nameBn = "সালাহউদ্দিন আয়ুবী গ্রুপ",
            nameEn = "Salahuddin Ayubi Group",
            leaderName = "নাসির উদ্দিন",
            leaderPhone = "01711-260103",
            subgroupMentors = "Ayubi-01: মো. ইব্রাহিম খলিল, Ayubi-02: মো. আবু বকর সিদ্দিক, Ayubi-03: মো. আবদুল্লাহ আল মামুন, Ayubi-04: মো. রাকিবুল ইসলাম",
            colorHex = 0xFFE65100,
            memberCount = 122,
            totalPoints = 950
        ),
        CampGroupEntity(
            id = "sumaiya",
            nameBn = "সুমাইয়া গ্রুপ",
            nameEn = "Sumaiya Group",
            leaderName = "শারমিন সুলতানা",
            leaderPhone = "01711-260104",
            subgroupMentors = "Sumaiya-01: উম্মে হাবিবা, Sumaiya-02: ফাতেমা আক্তার, Sumaiya-03: খাদিজা খাতুন, Sumaiya-04: আয়েশা সিদ্দিকা",
            colorHex = 0xFFC2185B,
            memberCount = 84,
            totalPoints = 935
        ),
        CampGroupEntity(
            id = "umm_ayman",
            nameBn = "উম্মে আইমান গ্রুপ",
            nameEn = "Umme Ayman Group",
            leaderName = "তাহমিনা বিনতে নূর",
            leaderPhone = "01711-260105",
            subgroupMentors = "Umme Ayman-01: মরিয়ম আক্তার, Umme Ayman-02: সুমাইয়া বিনতে আলম, Umme Ayman-03: নুসরাত জাহান, Umme Ayman-04: সাদিয়া আফরিন",
            colorHex = 0xFF7B1FA2,
            memberCount = 89,
            totalPoints = 930
        ),
        CampGroupEntity(
            id = "umm_saad",
            nameBn = "উম্মে সা’দ (হাম্বলী) গ্রুপ",
            nameEn = "Umme Sa'ad (Hambali) Group",
            leaderName = "হামিদা আক্তার",
            leaderPhone = "01711-260106",
            subgroupMentors = "Umme Sa'ad-01: হাফসা বিনতে করিম, Umme Sa'ad-02: জয়নব আক্তার, Umme Sa'ad-03: রুকাইয়া সুলতানা, Umme Sa'ad-04: আসমাউল হুসনা",
            colorHex = 0xFF0288D1,
            memberCount = 71,
            totalPoints = 920
        )
    )

    val stations = listOf(
        StationEntity(
            id = 1,
            nameBn = "স্টেশন-১: সাহিত্য-সাংস্কৃতিক প্রতিযোগিতা ও শিষ্টাচার ওয়ার্কশপ",
            nameEn = "Station 1: Literary Competitions & Public Manners Workshop",
            moderatorName = "মুহাম্মদ শাহাদাত হোসাইন ও ওমর ফারুক সময়",
            location = "স্টেশন জোন-১ (অডিটোরিয়াম উইং)",
            objectiveBn = "কুরআন, হাদীস, সাধারণ জ্ঞান, কবিতা, দোয়া, চিত্রাঙ্কন ও পাবলিক ম্যানারস রোল প্লে",
            iconKey = "quran",
            materialsNeeded = "কুরআন শরীফ, কুইজ শিট, ড্রয়িং আর্ট পেপার, রোল প্লে কার্ড, পুরস্কার ফর্ম",
            day2TopicBn = "কুরআন তিলাওয়াত, হাদীস পাঠ ও সাধারণ জ্ঞান প্রতিযোগিতা",
            day4TopicBn = "পাবলিক ম্যানারস ও আদব-কায়দা রোল প্লে (ওমর ফারুক সময়)",
            day5TopicBn = "কবিতা আবৃত্তি, মাসনূন দোয়া ও চিত্রাঙ্কন প্রতিযোগিতা",
            subModerators = "বিচারক প্যানেল ও ২৪ সাব-গ্রুপ ইনচার্জবৃন্দ"
        ),
        StationEntity(
            id = 2,
            nameBn = "স্টেশন-২: লাইফ স্কিল ও ব্যবহারিক দক্ষতা",
            nameEn = "Station 2: Practical Life Skills, First Aid & Problem Solving",
            moderatorName = "সাইফুল ইসলাম ও মোহাম্মদ আল আমিন কবির",
            location = "স্টেশন জোন-২ (প্রধান মাঠ ও ক্রাফট প্যাভিলিয়ন)",
            objectiveBn = "নিরাপত্তা ও ৯৯৯, ফার্স্ট এইড, সিপিআর, ফায়ার ড্রিল, অরিগ্যামী-হ্যান্ডক্রাফট, দেয়ালিকা ও প্রবলেম সলভিং",
            iconKey = "fitness",
            materialsNeeded = "ফার্স্ট এইড বক্স, সিপিআর ম্যানিকিন, ফায়ার এক্সটিংগুইশার, অরিগ্যামী পেপার, দেয়ালিকা বোর্ড",
            day2TopicBn = "আত্মরক্ষা ও নিরাপত্তা, ৯৯৯, ফার্স্ট এইড, সিপিআর ও ফায়ার ড্রিল (মোহাম্মদ আল আমিন কবির / ফায়ার সার্ভিস)",
            day4TopicBn = "অরিগ্যামী, হ্যান্ড ক্রাফট ও ২৪ সাব-গ্রুপ দেয়ালিকা তৈরি",
            day5TopicBn = "প্রবলেম সলভিং ও সিদ্ধান্ত গ্রহণ ওয়ার্কশপ (ওমর ফারুক সময়)",
            subModerators = "তাকিয়া রুফাইদা, সানিয়া আক্তার, উম্মেহানী লুবনা ও ফায়ার সার্ভিস টিম"
        ),
        StationEntity(
            id = 3,
            nameBn = "স্টেশন-৩: শিশুতোষ আলোচনা ও নৈতিক বিকাশ",
            nameEn = "Station 3: Child-Friendly Moral & Dream Talks",
            moderatorName = "ইঞ্জিনিয়ার আতিকুর রহমান",
            location = "স্টেশন জোন-৩ (সম্মেলন কক্ষ-১)",
            objectiveBn = "মৌলিক বিশ্বাস, উত্তম চরিত্র, আত্মগঠন ও বড় হওয়ার স্বপ্ন জাগ্রত করা",
            iconKey = "speech",
            materialsNeeded = "প্রজেক্টর, সাউন্ড সিস্টেম, নোটপ্যাড, অনুপ্রেরণামূলক স্লাইড",
            day2TopicBn = "মৌলিক বিশ্বাস, নৈতিকতা ও উত্তম চরিত্র (ড. কামরুল হাসান শাহীন)",
            day4TopicBn = "নিজকে গড়ো, গড়তে শেখাও (আবরারুল হক)",
            day5TopicBn = "মোরা বড় হতে চাই — স্বপ্ন ও ক্যারিয়ার (নাঈম আল ইসলাম মাহিন)",
            subModerators = "ড. কামরুল হাসান শাহীন, আবরারুল হক, নাঈম আল ইসলাম মাহিন"
        ),
        StationEntity(
            id = 4,
            nameBn = "স্টেশন-৪: ফিকাহ ও সালাত ওয়ার্কশপ",
            nameEn = "Station 4: Practical Fiqh, Taharah & Salah Workshop",
            moderatorName = "এ. এম. ইউসুফ",
            location = "স্টেশন জোন-৪ (কেন্দ্রীয় মসজিদ ও নামায হল)",
            objectiveBn = "সহীহ অজু-তাহারাত, নামাযের মাসলা-মাসায়েল ও পঠিত দোয়া-সূরার অর্থ অনুশীলন",
            iconKey = "quran",
            materialsNeeded = "জায়নামায, অজু ও সালাত চার্ট, মাসনূন দোয়া বুকলেট",
            day2TopicBn = "নামাযের প্রয়োজনীয় মাসলা-মাসায়েল ও বাস্তব অনুশীলন",
            day4TopicBn = "পবিত্রতা, অজু ও তাহারাতের ব্যবহারিক প্রশিক্ষণ",
            day5TopicBn = "নামাযে পঠিত দোয়া ও সূরাসমূহের অর্থ অনুধাবন",
            subModerators = "নূর উদ্দিন সিয়াম, মর্জিনা খাতুন"
        ),
        StationEntity(
            id = 5,
            nameBn = "স্টেশন-৫: রিফ্রেকটিভ সেশন ও লিডারশীপ",
            nameEn = "Station 5: Reflective Session, Dream & Team Building",
            moderatorName = "মাইজুরা আইলিন ও আবদুল্লাহ",
            location = "স্টেশন জোন-৫ (লেকসাইড প্যাভিলিয়ন)",
            objectiveBn = "আত্মউন্নয়ন, নতুন বন্ধু তৈরি (মিতালি), ড্রিম বিল্ডিং, লিডারশীপ ও টিম বিল্ডিং",
            iconKey = "teamwork",
            materialsNeeded = "ড্রিম কার্ড, টিম বিল্ডিং কিট, মার্কার, পোস্টার পেপার",
            day2TopicBn = "আত্মউন্নয়নমূলক একটিভিটিজ ও নতুন বন্ধু তৈরি (মিতালি)",
            day4TopicBn = "ড্রিম বিল্ডিং ও লিডারশীপ ওয়ার্কশপ",
            day5TopicBn = "টিম বিল্ডিং একটিভিটিজ ও দলগত সমন্বয়",
            subModerators = "ওমর ফারুক, শিহাব ইবনে আক্তার, ইশফাকুর রহমান, মিরাজ হাসান"
        ),
        StationEntity(
            id = 6,
            nameBn = "স্টেশন-৬: মেডিকেল, ডেন্টাল চেকআপ ও আই ক্যাম্প",
            nameEn = "Station 6: Medical Checkup, Mobile Dental Bus & Eye Camp",
            moderatorName = "ডা. ওরাকাতুল জান্নাত ও মোস্তাফিজুর রহমান গাজী",
            location = "স্টেশন জোন-৬ (মেডিকেল ক্যাম্প ও ভ্রাম্যমাণ ডেন্টাল বাস)",
            objectiveBn = "৬০৯ জন শিক্ষার্থীর পূর্ণাঙ্গ স্বাস্থ্য পরীক্ষা, উচ্চতা-ওজন, দন্ত পরীক্ষা ও চক্ষু স্ক্রিনিং",
            iconKey = "medical",
            materialsNeeded = "স্টেথোস্কোপ, ওজন ও উচ্চতা মাপার যন্ত্র, ভিশন চার্ট, ভ্রাম্যমাণ ডেন্টাল বাস, প্রেসক্রিপশন ফর্ম",
            day2TopicBn = "সাধারণ স্বাস্থ্য পরীক্ষা, উচ্চতা-ওজন পরিমাপ ও স্বাস্থ্য সচেতনতা কুইজ",
            day4TopicBn = "দন্ত পরীক্ষা, ভ্রাম্যমাণ ডেন্টাল বাস ও টুথব্রাশ-পেস্ট সচেতনতা লিফলেট",
            day5TopicBn = "চক্ষু পরীক্ষা (আই ক্যাম্প) ও ভিশন স্ক্রিনিং",
            subModerators = "শফিকুল ইসলাম বাঁধন, কাজী নজরুল ইসলাম ও ৮ জন বিশেষজ্ঞ ডাক্তার"
        )
    )

    /**
     * Generates all 609 real orphan participants from the official 2026 Word document roster (Tables 26–49).
     */
    fun generateParticipants(): List<ParticipantEntity> = OfficialRoster2026.buildOfficialParticipants()

    // Official 6-Slot Cyclic Morning Rotation Matrix (Tables 6, 9, 10: 09:00 AM - 01:30 PM on Days 2, 4, 5)
    const val ROTATION_SLOT_1 = "bukhari:1,salahuddin:2,umm_ayman:3,sumaiya:4,umm_saad:5,shafeyi:6"
    const val ROTATION_SLOT_2 = "shafeyi:1,bukhari:2,salahuddin:3,umm_ayman:4,sumaiya:5,umm_saad:6"
    const val ROTATION_SLOT_3 = "umm_saad:1,shafeyi:2,bukhari:3,salahuddin:4,umm_ayman:5,sumaiya:6"
    const val ROTATION_SLOT_4 = "sumaiya:1,umm_saad:2,shafeyi:3,bukhari:4,salahuddin:5,umm_ayman:6"
    const val ROTATION_SLOT_5 = "umm_ayman:1,sumaiya:2,umm_saad:3,shafeyi:4,bukhari:5,salahuddin:6"
    const val ROTATION_SLOT_6 = "salahuddin:1,umm_ayman:2,sumaiya:3,umm_saad:4,shafeyi:5,bukhari:6"

    val scheduleEvents = listOf(
        // ==================== DAY 1: ২২ অক্টোবর ২০২৬ (বৃহস্পতিবার) ====================
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "07:00 - 11:00",
            titleBn = "ক্যাম্পে আগমন, অভ্যর্থনা, রেজিস্ট্রেশন ও রুম বরাদ্দ",
            titleEn = "Arrival, Welcome Reception, Registration & Room Allocation",
            category = "Transport",
            location = "রাঙামাটি ওয়াটারফ্রন্ট রিসোর্ট অভ্যর্থনা চত্বর",
            isCompleted = true
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "11:00 - 01:00",
            titleBn = "গ্রুপ ওরিয়েন্টেশন, আইডি ব্যাজ ও ৩০টি উপহার সামগ্রী বিতরণ",
            titleEn = "Group Orientation, ID Badge & 30-Item Gift Kit Distribution",
            category = "Halaqa",
            location = "প্লাটিনাম, গোল্ডেন ও রজনীগন্ধা আবাসন জোন",
            isCompleted = true
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "01:15 - 02:45",
            titleBn = "যোহর সালাত, দুপুরের খাবার ও বিশ্রাম",
            titleEn = "Dhuhr Prayer, Lunch & Rest",
            category = "Meal",
            location = "কেন্দ্রীয় মসজিদ ও ডাইনিং হল"
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "03:00 - 04:30",
            titleBn = "সাব-গ্রুপ পরিচিতি (Self Introduction), নিয়মাবলী ব্রিফিং ও প্যারেড প্রস্তুতি",
            titleEn = "Subgroup Self-Introduction, Camp Rules Briefing & Parade Prep",
            category = "Physical",
            location = "প্রধান প্যারেড গ্রাউন্ড ও সাব-গ্রুপ কর্নার"
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "04:45 - 06:00",
            titleBn = "আসর সালাত ও গ্র্যান্ড উদ্বোধনী অধিবেশন (নিজকে গড়ো, গড়তে শেখাও)",
            titleEn = "Asr Prayer & Grand Opening Ceremony",
            category = "Halaqa",
            location = "কেন্দ্রীয় মিলনায়তন (Central Auditorium)"
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "06:00 - 07:15",
            titleBn = "মাগরিব সালাত, মাসনূন যিকির ও সান্ধ্যকালীন নাস্তা",
            titleEn = "Maghrib Prayer, Evening Azkar & Snacks",
            category = "Jamaat",
            location = "কেন্দ্রীয় মসজিদ ও মেয়েদের নামায হল"
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "07:15 - 08:45",
            titleBn = "সাংস্কৃতিক সন্ধ্যা-১ (পরিবেশনায়: ঢাকা ড্রিমহোমস, উম্মে সা’দ ও আইয়ুবী গ্রুপ)",
            titleEn = "Cultural Night 1 (Dhaka Dreamhomes, Umme Sa'ad & Ayubi Groups)",
            category = "Competition",
            location = "কেন্দ্রীয় মুক্তমঞ্চ (Open Stage)"
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "08:45 - 10:00",
            titleBn = "এশা সালাত, রাতের খাবার, সাব-গ্রুপ পর্যালোচনা ও লাইটস অফ",
            titleEn = "Isha Prayer, Dinner, Subgroup Night Check & Lights Off",
            category = "Meal",
            location = "ডাইনিং হল ও আবাসিক কটেজ"
        ),

        // ==================== DAY 2: ২৩ অক্টোবর ২০২৬ (শুক্রবার) ====================
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "05:00 - 06:15",
            titleBn = "ফজর জামাত, কুরআন তিলাওয়াত ও দারসুল কুরআন",
            titleEn = "Fajr Jamaat, Quran Tilawat & Morning Dars",
            category = "Jamaat",
            location = "কেন্দ্রীয় মসজিদ ও নামায হল"
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "06:30 - 07:30",
            titleBn = "প্রভাতী পিটি, শরীরচর্চা ও প্যারেড অনুশীলন",
            titleEn = "Morning PT, Fitness & Parade Practice",
            category = "Physical",
            location = "প্রধান প্যারেড মাঠ"
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "07:30 - 08:45",
            titleBn = "সকালের নাস্তা ও স্টেশন রোটেশন প্রস্তুতি",
            titleEn = "Breakfast & Station Rotation Preparation",
            category = "Meal",
            location = "ডাইনিং হল"
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "09:00 - 09:45",
            titleBn = "স্টেশন রোটেশন (স্লট-১) — ২য় দিনের বিষয়ভিত্তিক সেশন",
            titleEn = "Morning Station Rotation (Slot 1 of 6)",
            category = "Station",
            stationId = 1,
            location = "স্টেশন জোন ১–৬ (রাঙামাটি রিসোর্ট)",
            isCurrent = true,
            rotationJson = ROTATION_SLOT_1
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "09:45 - 10:30",
            titleBn = "স্টেশন রোটেশন (স্লট-২) — ২য় দিনের বিষয়ভিত্তিক সেশন",
            titleEn = "Morning Station Rotation (Slot 2 of 6)",
            category = "Station",
            stationId = 2,
            location = "স্টেশন জোন ১–৬ (রাঙামাটি রিসোর্ট)",
            rotationJson = ROTATION_SLOT_2
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "10:30 - 11:15",
            titleBn = "স্টেশন রোটেশন (স্লট-৩) — ২য় দিনের বিষয়ভিত্তিক সেশন",
            titleEn = "Morning Station Rotation (Slot 3 of 6)",
            category = "Station",
            stationId = 3,
            location = "স্টেশন জোন ১–৬ (রাঙামাটি রিসোর্ট)",
            rotationJson = ROTATION_SLOT_3
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "11:15 - 12:00",
            titleBn = "স্টেশন রোটেশন (স্লট-৪) — ২য় দিনের বিষয়ভিত্তিক সেশন",
            titleEn = "Morning Station Rotation (Slot 4 of 6)",
            category = "Station",
            stationId = 4,
            location = "স্টেশন জোন ১–৬ (রাঙামাটি রিসোর্ট)",
            rotationJson = ROTATION_SLOT_4
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "12:00 - 12:45",
            titleBn = "স্টেশন রোটেশন (স্লট-৫) — ২য় দিনের বিষয়ভিত্তিক সেশন",
            titleEn = "Morning Station Rotation (Slot 5 of 6)",
            category = "Station",
            stationId = 5,
            location = "স্টেশন জোন ১–৬ (রাঙামাটি রিসোর্ট)",
            rotationJson = ROTATION_SLOT_5
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "12:45 - 01:30",
            titleBn = "স্টেশন রোটেশন (স্লট-৬) — ২য় দিনের বিষয়ভিত্তিক সেশন",
            titleEn = "Morning Station Rotation (Slot 6 of 6)",
            category = "Station",
            stationId = 6,
            location = "স্টেশন জোন ১–৬ (রাঙামাটি রিসোর্ট)",
            rotationJson = ROTATION_SLOT_6
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "01:30 - 03:00",
            titleBn = "পবিত্র জুমু’আর সালাত, বিশেষ মধ্যাহ্নভোজ ও বিশ্রাম",
            titleEn = "Jumu'ah Prayer, Special Lunch & Rest",
            category = "Meal",
            location = "কেন্দ্রীয় মসজিদ ও ডাইনিং হল"
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "03:00 - 03:30",
            titleBn = "রুম ও গ্রুপ পরিচ্ছন্নতা পরিদর্শন (Group & Room Inspection)",
            titleEn = "Official Group & Dormitory Cleanliness Inspection",
            category = "Halaqa",
            location = "প্লাটিনাম, গোল্ডেন ও রজনীগন্ধা কটেজসমূহ"
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "03:30 - 05:30",
            titleBn = "আসর সালাত, ক্যাম্প হাইকিং ও আউটডোর গেমস প্রতিযোগিতা (১৪৪টি পুরস্কার)",
            titleEn = "Asr Prayer, Camp Hiking & Outdoor Games (144 Prizes)",
            category = "Physical",
            location = "রিসোর্ট হাইকিং ট্রেইল ও খেলার মাঠ"
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "06:00 - 07:15",
            titleBn = "মাগরিব সালাত ও উপস্থিত বক্তৃতা প্রতিযোগিতা (২৪ সাব-গ্রুপ)",
            titleEn = "Maghrib Prayer & Extempore Speech Competition",
            category = "Competition",
            location = "সাব-গ্রুপ ভেন্যু ও মিলনায়তন"
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "07:15 - 08:45",
            titleBn = "সাংস্কৃতিক সন্ধ্যা-২ (পরিবেশনায়: তাসফিয়া ড্রিমহোমস, উম্মে আইমান ও সুমাইয়া গ্রুপ)",
            titleEn = "Cultural Night 2 (Tasfiya Dreamhomes, Umme Ayman & Sumaiya Groups)",
            category = "Competition",
            location = "কেন্দ্রীয় মুক্তমঞ্চ"
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "08:45 - 10:00",
            titleBn = "এশা সালাত, রাতের খাবার, ঢাকা সফরের ব্রিফিং ও লাইটস অফ",
            titleEn = "Isha Prayer, Dinner, Dhaka Study Tour Briefing & Lights Off",
            category = "Meal",
            location = "ডাইনিং হল ও আবাসিক কটেজ"
        ),

        // ==================== DAY 3: ২৪ অক্টোবর ২০২৬ (শনিবার — শিক্ষা সফর) ====================
        ScheduleEventEntity(
            dayNo = 3,
            timeSlot = "05:00 - 06:30",
            titleBn = "ফজর সালাত, নাস্তা গ্রহণ ও ১৬টি বাসে সাব-গ্রুপ রোল-কল বোর্ডিং",
            titleEn = "Fajr Prayer, Breakfast & 16-Bus Subgroup Roll-Call Boarding",
            category = "Transport",
            location = "রিসোর্ট বাস পার্কিং জোন"
        ),
        ScheduleEventEntity(
            dayNo = 3,
            timeSlot = "08:30 - 11:30",
            titleBn = "বাংলাদেশ জাতীয় চিড়িয়াখানা পরিদর্শন ও প্রকৃতি পর্যবেক্ষণ",
            titleEn = "Bangladesh National Zoo Visit & Nature Study",
            category = "Halaqa",
            location = "জাতীয় চিড়িয়াখানা, মিরপুর, ঢাকা"
        ),
        ScheduleEventEntity(
            dayNo = 3,
            timeSlot = "11:45 - 01:15",
            titleBn = "জাতীয় সংসদ ভবন প্রাঙ্গণ ও ঢাকা বিশ্ববিদ্যালয় / জাতীয় কবির সমাধি পরিদর্শন",
            titleEn = "National Parliament, Dhaka University & National Poet's Mausoleum",
            category = "Halaqa",
            location = "শেরেবাংলা নগর ও ঢাকা বিশ্ববিদ্যালয় এলাকা"
        ),
        ScheduleEventEntity(
            dayNo = 3,
            timeSlot = "01:30 - 02:45",
            titleBn = "জাতীয় মসজিদ বায়তুল মোকাররমে যোহর সালাত ও বিশেষ দুপুরের খাবার",
            titleEn = "Dhuhr Prayer at Baitul Mukarram National Mosque & Special Lunch",
            category = "Meal",
            location = "বায়তুল মোকাররম জাতীয় মসজিদ প্রাঙ্গণ, ঢাকা"
        ),
        ScheduleEventEntity(
            dayNo = 3,
            timeSlot = "03:00 - 05:30",
            titleBn = "বাংলাদেশ সামরিক জাদুঘর পরিদর্শন ও আসর সালাত",
            titleEn = "Bangladesh Military Museum Visit & Asr Prayer",
            category = "Physical",
            location = "বিজয় সরণি সামরিক জাদুঘর, ঢাকা"
        ),
        ScheduleEventEntity(
            dayNo = 3,
            timeSlot = "06:00 - 08:30",
            titleBn = "আনন্দময় মেট্রোরেল ভ্রমণ (Metro Rail Ride) ও আউটিং জ্ঞান অন্বেষণ কুইজ",
            titleEn = "Exciting Dhaka Metro Rail Ride & Outing Knowledge Quest (24 Prizes)",
            category = "Transport",
            location = "ঢাকা মেট্রোরেল স্টেশন (উত্তরা–আগারগাঁও–মতিঝিল রুট)"
        ),
        ScheduleEventEntity(
            dayNo = 3,
            timeSlot = "08:45 - 10:15",
            titleBn = "রিসোর্টে নিরাপদ প্রত্যাবর্তন, শতভাগ হেডকাউন্ট যাচাই, ডিনার ও বিশ্রাম",
            titleEn = "Safe Return to Resort, 100% Headcount Verification, Dinner & Rest",
            category = "Meal",
            location = "রাঙামাটি ওয়াটারফ্রন্ট রিসোর্ট"
        ),

        // ==================== DAY 4: ২৫ অক্টোবর ২০২৬ (রবিবার) ====================
        ScheduleEventEntity(
            dayNo = 4,
            timeSlot = "05:00 - 06:15",
            titleBn = "ফজর জামাত, কুরআন তিলাওয়াত ও সকালের যিকির",
            titleEn = "Fajr Jamaat, Quran Recitation & Morning Azkar",
            category = "Jamaat",
            location = "কেন্দ্রীয় মসজিদ ও নামায হল"
        ),
        ScheduleEventEntity(
            dayNo = 4,
            timeSlot = "06:30 - 07:30",
            titleBn = "প্রভাতী পিটি ও প্যারেড কমান্ড রিহার্সাল",
            titleEn = "Morning PT & Parade Command Rehearsal",
            category = "Physical",
            location = "প্রধান প্যারেড মাঠ"
        ),
        ScheduleEventEntity(
            dayNo = 4,
            timeSlot = "07:30 - 08:45",
            titleBn = "সকালের নাস্তা ও ৪র্থ দিনের স্টেশন প্রস্তুতি",
            titleEn = "Breakfast & Day 4 Station Prep",
            category = "Meal",
            location = "ডাইনিং হল"
        ),
        ScheduleEventEntity(
            dayNo = 4,
            timeSlot = "09:00 - 09:45",
            titleBn = "স্টেশন রোটেশন (স্লট-১) — রোল প্লে, ক্রাফট, ডেন্টাল বাস ও ড্রিম বিল্ডিং",
            titleEn = "Day 4 Morning Station Rotation (Slot 1 of 6)",
            category = "Station",
            stationId = 1,
            location = "স্টেশন জোন ১–৬ ও ভ্রাম্যমাণ ডেন্টাল বাস",
            rotationJson = ROTATION_SLOT_1
        ),
        ScheduleEventEntity(
            dayNo = 4,
            timeSlot = "09:45 - 10:30",
            titleBn = "স্টেশন রোটেশন (স্লট-২) — রোল প্লে, ক্রাফট, ডেন্টাল বাস ও ড্রিম বিল্ডিং",
            titleEn = "Day 4 Morning Station Rotation (Slot 2 of 6)",
            category = "Station",
            stationId = 2,
            location = "স্টেশন জোন ১–৬ ও ভ্রাম্যমাণ ডেন্টাল বাস",
            rotationJson = ROTATION_SLOT_2
        ),
        ScheduleEventEntity(
            dayNo = 4,
            timeSlot = "10:30 - 11:15",
            titleBn = "স্টেশন রোটেশন (স্লট-৩) — রোল প্লে, ক্রাফট, ডেন্টাল বাস ও ড্রিম বিল্ডিং",
            titleEn = "Day 4 Morning Station Rotation (Slot 3 of 6)",
            category = "Station",
            stationId = 3,
            location = "স্টেশন জোন ১–৬ ও ভ্রাম্যমাণ ডেন্টাল বাস",
            rotationJson = ROTATION_SLOT_3
        ),
        ScheduleEventEntity(
            dayNo = 4,
            timeSlot = "11:15 - 12:00",
            titleBn = "স্টেশন রোটেশন (স্লট-৪) — রোল প্লে, ক্রাফট, ডেন্টাল বাস ও ড্রিম বিল্ডিং",
            titleEn = "Day 4 Morning Station Rotation (Slot 4 of 6)",
            category = "Station",
            stationId = 4,
            location = "স্টেশন জোন ১–৬ ও ভ্রাম্যমাণ ডেন্টাল বাস",
            rotationJson = ROTATION_SLOT_4
        ),
        ScheduleEventEntity(
            dayNo = 4,
            timeSlot = "12:00 - 12:45",
            titleBn = "স্টেশন রোটেশন (স্লট-৫) — রোল প্লে, ক্রাফট, ডেন্টাল বাস ও ড্রিম বিল্ডিং",
            titleEn = "Day 4 Morning Station Rotation (Slot 5 of 6)",
            category = "Station",
            stationId = 5,
            location = "স্টেশন জোন ১–৬ ও ভ্রাম্যমাণ ডেন্টাল বাস",
            rotationJson = ROTATION_SLOT_5
        ),
        ScheduleEventEntity(
            dayNo = 4,
            timeSlot = "12:45 - 01:30",
            titleBn = "স্টেশন রোটেশন (স্লট-৬) — রোল প্লে, ক্রাফট, ডেন্টাল বাস ও ড্রিম বিল্ডিং",
            titleEn = "Day 4 Morning Station Rotation (Slot 6 of 6)",
            category = "Station",
            stationId = 6,
            location = "স্টেশন জোন ১–৬ ও ভ্রাম্যমাণ ডেন্টাল বাস",
            rotationJson = ROTATION_SLOT_6
        ),
        ScheduleEventEntity(
            dayNo = 4,
            timeSlot = "01:30 - 02:45",
            titleBn = "যোহর সালাত ও দুপুরের খাবার",
            titleEn = "Dhuhr Prayer & Lunch",
            category = "Meal",
            location = "কেন্দ্রীয় মসজিদ ও ডাইনিং হল"
        ),
        ScheduleEventEntity(
            dayNo = 4,
            timeSlot = "03:00 - 06:00",
            titleBn = "নন্দন পার্ক আনন্দ ভ্রমণ ও রাইড উপভোগ (Nandan Park Tour)",
            titleEn = "Afternoon Excursion & Rides at Nandan Park",
            category = "Transport",
            location = "নন্দন পার্ক, চন্দ্রা, গাজীপুর"
        ),
        ScheduleEventEntity(
            dayNo = 4,
            timeSlot = "07:15 - 08:45",
            titleBn = "সাংস্কৃতিক সন্ধ্যা-৩ ও নাটিকা (পরিবেশনায়: ওয়ামী ড্রিমহোমস, বুখারী ও শাফে’য়ী গ্রুপ)",
            titleEn = "Cultural Night 3 (WAMY Dreamhomes, Bukhari & Shafi'ee Groups)",
            category = "Competition",
            location = "কেন্দ্রীয় মুক্তমঞ্চ"
        ),
        ScheduleEventEntity(
            dayNo = 4,
            timeSlot = "08:45 - 10:00",
            titleBn = "এশা সালাত, রাতের খাবার ও দেয়ালিকা চূড়ান্ত জমাদান",
            titleEn = "Isha Prayer, Dinner & Final Wall Magazine Submission",
            category = "Meal",
            location = "ডাইনিং হল ও মিডিয়া সেল"
        ),

        // ==================== DAY 5: ২৬ অক্টোবর ২০২৬ (সোমবার — সমাপনী) ====================
        ScheduleEventEntity(
            dayNo = 5,
            timeSlot = "04:15 - 06:00",
            titleBn = "কিয়ামুল লাইল (তাহাজ্জুদ), ফজর জামাত ও সমাপনী দোয়া",
            titleEn = "Qiyamul Lail (Tahajjud), Fajr Jamaat & Spiritual Munajat",
            category = "Jamaat",
            location = "কেন্দ্রীয় মসজিদ ও নামায হল"
        ),
        ScheduleEventEntity(
            dayNo = 5,
            timeSlot = "06:30 - 08:30",
            titleBn = "গ্র্যান্ড প্যারেড চূড়ান্ত মহড়া ও সকালের নাস্তা",
            titleEn = "Grand Parade Final Rehearsal & Breakfast",
            category = "Meal",
            location = "প্রধান মাঠ ও ডাইনিং হল"
        ),
        ScheduleEventEntity(
            dayNo = 5,
            timeSlot = "09:00 - 09:45",
            titleBn = "স্টেশন রোটেশন (স্লট-১) — কবিতা/দোয়া/চিত্রাঙ্কন, প্রবলেম সলভিং ও আই ক্যাম্প",
            titleEn = "Day 5 Morning Station Rotation (Slot 1 of 6)",
            category = "Station",
            stationId = 1,
            location = "স্টেশন জোন ১–৬ ও আই ক্যাম্প বুথ",
            rotationJson = ROTATION_SLOT_1
        ),
        ScheduleEventEntity(
            dayNo = 5,
            timeSlot = "09:45 - 10:30",
            titleBn = "স্টেশন রোটেশন (স্লট-২) — কবিতা/দোয়া/চিত্রাঙ্কন, প্রবলেম সলভিং ও আই ক্যাম্প",
            titleEn = "Day 5 Morning Station Rotation (Slot 2 of 6)",
            category = "Station",
            stationId = 2,
            location = "স্টেশন জোন ১–৬ ও আই ক্যাম্প বুথ",
            rotationJson = ROTATION_SLOT_2
        ),
        ScheduleEventEntity(
            dayNo = 5,
            timeSlot = "10:30 - 11:15",
            titleBn = "স্টেশন রোটেশন (স্লট-৩) — কবিতা/দোয়া/চিত্রাঙ্কন, প্রবলেম সলভিং ও আই ক্যাম্প",
            titleEn = "Day 5 Morning Station Rotation (Slot 3 of 6)",
            category = "Station",
            stationId = 3,
            location = "স্টেশন জোন ১–৬ ও আই ক্যাম্প বুথ",
            rotationJson = ROTATION_SLOT_3
        ),
        ScheduleEventEntity(
            dayNo = 5,
            timeSlot = "11:15 - 12:00",
            titleBn = "স্টেশন রোটেশন (স্লট-৪) — কবিতা/দোয়া/চিত্রাঙ্কন, প্রবলেম সলভিং ও আই ক্যাম্প",
            titleEn = "Day 5 Morning Station Rotation (Slot 4 of 6)",
            category = "Station",
            stationId = 4,
            location = "স্টেশন জোন ১–৬ ও আই ক্যাম্প বুথ",
            rotationJson = ROTATION_SLOT_4
        ),
        ScheduleEventEntity(
            dayNo = 5,
            timeSlot = "12:00 - 12:45",
            titleBn = "স্টেশন রোটেশন (স্লট-৫) — কবিতা/দোয়া/চিত্রাঙ্কন, প্রবলেম সলভিং ও আই ক্যাম্প",
            titleEn = "Day 5 Morning Station Rotation (Slot 5 of 6)",
            category = "Station",
            stationId = 5,
            location = "স্টেশন জোন ১–৬ ও আই ক্যাম্প বুথ",
            rotationJson = ROTATION_SLOT_5
        ),
        ScheduleEventEntity(
            dayNo = 5,
            timeSlot = "12:45 - 01:30",
            titleBn = "স্টেশন রোটেশন (স্লট-৬) — কবিতা/দোয়া/চিত্রাঙ্কন, প্রবলেম সলভিং ও আই ক্যাম্প",
            titleEn = "Day 5 Morning Station Rotation (Slot 6 of 6)",
            category = "Station",
            stationId = 6,
            location = "স্টেশন জোন ১–৬ ও আই ক্যাম্প বুথ",
            rotationJson = ROTATION_SLOT_6
        ),
        ScheduleEventEntity(
            dayNo = 5,
            timeSlot = "01:30 - 02:30",
            titleBn = "যোহর সালাত, মধ্যাহ্নভোজ ও ২৪ সাব-গ্রুপ দেয়ালিকা প্রদর্শনী",
            titleEn = "Dhuhr Prayer, Lunch & 24-Subgroup Wall Magazine Exhibition",
            category = "Meal",
            location = "ডাইনিং হল ও দেয়ালিকা প্রদর্শনী গ্যালারি"
        ),
        ScheduleEventEntity(
            dayNo = 5,
            timeSlot = "02:30 - 04:30",
            titleBn = "অফিশিয়াল গ্র্যান্ড প্যারেড ও কুচকাওয়াজ প্রদর্শনী (২৪টি রেজিমেন্ট)",
            titleEn = "Official Grand Parade & 24-Regiment March-Past",
            category = "Physical",
            location = "প্রধান প্যারেড গ্রাউন্ড"
        ),
        ScheduleEventEntity(
            dayNo = 5,
            timeSlot = "04:30 - 06:00",
            titleBn = "আসর সালাত ও ঐতিহ্যবাহী গ্রামবাংলার পিঠা উৎসব (Pitha Utshab)",
            titleEn = "Asr Prayer & Traditional Bengali Pitha Festival",
            category = "Meal",
            location = "উৎসব চত্বর ও লেকসাইড লন"
        ),
        ScheduleEventEntity(
            dayNo = 5,
            timeSlot = "06:00 - 07:00",
            titleBn = "সমাপনী অধিবেশন, চ্যাম্পিয়ন ট্রফি, ৮৩৫টি পুরস্কার ও ১৮-স্টার সনদ প্রদান",
            titleEn = "Grand Closing Ceremony, 835 Prizes & 18-Star Certificate Awarding",
            category = "Competition",
            location = "কেন্দ্রীয় মিলনায়তন"
        ),
        ScheduleEventEntity(
            dayNo = 5,
            timeSlot = "07:15 - 09:00",
            titleBn = "মায়েদের উপহার চাদর ও গিফট ব্যাগ হস্তান্তর, বিদায়ী নৈশভোজ ও প্রস্থান",
            titleEn = "Mother's Gift Chador Handover, Farewell Dinner & Safe Departure",
            category = "Transport",
            location = "প্রধান ফটক ও পরিবহন জোন"
        )
    )

    val attendanceEvents = listOf(
        AttendanceEventEntity(
            id = 1,
            titleBn = "দিন ১ - ক্যাম্প আগমন ও রিসোর্ট চেক-ইন রোল-কল",
            titleEn = "Day 1 - Arrival & Resort Check-In Roll Call",
            dayNo = 1,
            timeLabel = "10:00 AM",
            targetCount = 609,
            presentCount = 609,
            lateCount = 0,
            absentCount = 0,
            isClosed = true
        ),
        AttendanceEventEntity(
            id = 2,
            titleBn = "দিন ২ - ফজর জামাত ও দারসুল কুরআন হাজিরা",
            titleEn = "Day 2 - Fajr Jamaat & Quran Session",
            dayNo = 2,
            timeLabel = "05:15 AM",
            targetCount = 609,
            presentCount = 604,
            lateCount = 5,
            absentCount = 0,
            isClosed = false
        ),
        AttendanceEventEntity(
            id = 4,
            titleBn = "দিন ২ - সকালের স্টেশন রোটেশন (স্টেশন ১–৬ কেন্দ্রীয় হাজিরা)",
            titleEn = "Day 2 - Morning Station Rotation Master Attendance",
            dayNo = 2,
            timeLabel = "09:00 AM",
            targetCount = 609,
            presentCount = 606,
            lateCount = 3,
            absentCount = 0,
            isClosed = false
        ),
        AttendanceEventEntity(
            id = 301,
            titleBn = "দিন ৩ - ঢাকা শিক্ষা সফর বাস বোর্ডিং ও মেট্রোরেল হেডকাউন্ট",
            titleEn = "Day 3 - Dhaka Study Tour Bus & Metro Rail Headcount",
            dayNo = 3,
            timeLabel = "06:30 AM",
            targetCount = 609,
            presentCount = 0,
            lateCount = 0,
            absentCount = 0,
            isClosed = false
        ),
        AttendanceEventEntity(
            id = 401,
            titleBn = "দিন ৪ - সকালের স্টেশন রোটেশন ও ডেন্টাল ক্যাম্প হাজিরা",
            titleEn = "Day 4 - Morning Station Rotation & Dental Camp",
            dayNo = 4,
            timeLabel = "09:00 AM",
            targetCount = 609,
            presentCount = 0,
            lateCount = 0,
            absentCount = 0,
            isClosed = false
        ),
        AttendanceEventEntity(
            id = 402,
            titleBn = "দিন ৪ - নন্দন পার্ক ভ্রমণ গেট ও রিটার্ন রোল-কল",
            titleEn = "Day 4 - Nandan Park Entry & Return Roll Call",
            dayNo = 4,
            timeLabel = "03:00 PM",
            targetCount = 609,
            presentCount = 0,
            lateCount = 0,
            absentCount = 0,
            isClosed = false
        ),
        AttendanceEventEntity(
            id = 501,
            titleBn = "দিন ৫ - শেষ দিনের স্টেশন রোটেশন ও আই ক্যাম্প হাজিরা",
            titleEn = "Day 5 - Final Station Rotation & Eye Camp",
            dayNo = 5,
            timeLabel = "09:00 AM",
            targetCount = 609,
            presentCount = 0,
            lateCount = 0,
            absentCount = 0,
            isClosed = false
        ),
        AttendanceEventEntity(
            id = 502,
            titleBn = "দিন ৫ - অফিশিয়াল প্যারেড ও সমাপনী সনদ বিতরণ হাজিরা",
            titleEn = "Day 5 - Grand Parade & Closing Ceremony",
            dayNo = 5,
            timeLabel = "02:30 PM",
            targetCount = 609,
            presentCount = 0,
            lateCount = 0,
            absentCount = 0,
            isClosed = false
        )
    )

    /**
     * All 21 Official Prize Categories from Table 20 = Exactly 835 Prizes!
     * Note: First 6 competitions map cleanly to the 6 core participant milestones.
     */
    val competitions = listOf(
        CompetitionEntity(
            id = 1,
            nameBn = "কুরআন তিলাওয়াত প্রতিযোগিতা",
            nameEn = "Quran Recitation Competition",
            category = "Quran",
            dayNo = 2,
            maxScore = 100,
            judgeName = "মুহাম্মদ শাহাদাত হোসাইন ও তাজবীদ বোর্ড",
            status = "LIVE",
            prizeCount = 72,
            isTeamEvent = false,
            selectionScope = "SUBGROUP"
        ),
        CompetitionEntity(
            id = 2,
            nameBn = "হাদীস পাঠ ও অর্থ প্রতিযোগিতা",
            nameEn = "Hadith Recitation & Meaning",
            category = "Hadith",
            dayNo = 2,
            maxScore = 100,
            judgeName = "এ. এম. ইউসুফ ও হাদীস বোর্ড",
            status = "LIVE",
            prizeCount = 72,
            isTeamEvent = false,
            selectionScope = "SUBGROUP"
        ),
        CompetitionEntity(
            id = 3,
            nameBn = "সাধারণ জ্ঞান ও ইসলামী কুইজ প্রতিযোগিতা",
            nameEn = "General & Islamic Knowledge Quiz",
            category = "Quiz",
            dayNo = 2,
            maxScore = 100,
            judgeName = "ইঞ্জিনিয়ার আতিকুর রহমান ও কুইজ প্যানেল",
            status = "LIVE",
            prizeCount = 72,
            isTeamEvent = false,
            selectionScope = "SUBGROUP"
        ),
        CompetitionEntity(
            id = 4,
            nameBn = "চিত্রাঙ্কন প্রতিযোগিতা",
            nameEn = "Drawing & Art Competition",
            category = "Arts",
            dayNo = 5,
            maxScore = 100,
            judgeName = "সাইফুল ইসলাম ও চারুকলা বিচারকবৃন্দ",
            status = "SCHEDULED",
            prizeCount = 72,
            isTeamEvent = false,
            selectionScope = "SUBGROUP"
        ),
        CompetitionEntity(
            id = 5,
            nameBn = "উপস্থিত বক্তৃতা প্রতিযোগিতা",
            nameEn = "Extempore Speech Competition",
            category = "Speech",
            dayNo = 2,
            maxScore = 100,
            judgeName = "ওমর ফারুক সময় ও বক্তৃতা জুরি বোর্ড",
            status = "LIVE",
            prizeCount = 72,
            isTeamEvent = false,
            selectionScope = "SUBGROUP"
        ),
        CompetitionEntity(
            id = 6,
            nameBn = "আউটডোর গেমস ও ক্রীড়া প্রতিযোগিতা (২টি ইভেন্ট)",
            nameEn = "Outdoor Games & Athletics (2 Events)",
            category = "Sports",
            dayNo = 2,
            maxScore = 100,
            judgeName = "মোহাম্মদ আল আমিন কবির ও ক্রীড়া টিম",
            status = "LIVE",
            prizeCount = 144,
            isTeamEvent = false,
            selectionScope = "SUBGROUP"
        ),
        CompetitionEntity(
            id = 7,
            nameBn = "কবিতা আবৃত্তি প্রতিযোগিতা",
            nameEn = "Poetry Recitation Competition",
            category = "Speech",
            dayNo = 5,
            maxScore = 100,
            judgeName = "মুহাম্মদ শাহাদাত হোসাইন ও সাহিত্য প্যানেল",
            status = "SCHEDULED",
            prizeCount = 72,
            isTeamEvent = false,
            selectionScope = "SUBGROUP"
        ),
        CompetitionEntity(
            id = 8,
            nameBn = "মাসনূন দোয়া প্রতিযোগিতা",
            nameEn = "Masnoon Dua Memorization",
            category = "Quran",
            dayNo = 5,
            maxScore = 100,
            judgeName = "নূর উদ্দিন সিয়াম ও মর্জিনা খাতুন",
            status = "SCHEDULED",
            prizeCount = 72,
            isTeamEvent = false,
            selectionScope = "SUBGROUP"
        ),
        CompetitionEntity(
            id = 9,
            nameBn = "সৃজনশীল হস্তশিল্প (অরিগ্যামী ও হ্যান্ড ক্রাফট)",
            nameEn = "Origami & Creative Handcraft",
            category = "Arts",
            dayNo = 4,
            maxScore = 100,
            judgeName = "তাকিয়া রুফাইদা, সানিয়া আক্তার ও উম্মেহানী লুবনা",
            status = "SCHEDULED",
            prizeCount = 72,
            isTeamEvent = false,
            selectionScope = "SUBGROUP"
        ),
        CompetitionEntity(
            id = 10,
            nameBn = "পাবলিক ম্যানারস রোল প্লে (শ্রেষ্ঠ অভিনয়)",
            nameEn = "Public Manners Role Play",
            category = "Speech",
            dayNo = 4,
            maxScore = 100,
            judgeName = "ওমর ফারুক সময়",
            status = "SCHEDULED",
            prizeCount = 18,
            isTeamEvent = false,
            selectionScope = "GROUP"
        ),
        CompetitionEntity(
            id = 11,
            nameBn = "রিফ্রেকটিভ সেশন অ্যাক্টিভ পারফর্মার",
            nameEn = "Reflective Session Active Performer",
            category = "Leadership",
            dayNo = 4,
            maxScore = 100,
            judgeName = "মাইজুরা আইলিন ও আবদুল্লাহ",
            status = "SCHEDULED",
            prizeCount = 18,
            isTeamEvent = false,
            selectionScope = "GROUP"
        ),
        CompetitionEntity(
            id = 12,
            nameBn = "আউটিং জ্ঞান অন্বেষণ কুইজ (ঢাকা শিক্ষা সফর)",
            nameEn = "Study Tour Knowledge Quest",
            category = "Quiz",
            dayNo = 3,
            maxScore = 100,
            judgeName = "আমিনুল ইসলাম বুলবুল ও সফর মূল্যায়ন টিম",
            status = "SCHEDULED",
            prizeCount = 24,
            isTeamEvent = false,
            selectionScope = "SUBGROUP"
        ),
        CompetitionEntity(
            id = 13,
            nameBn = "শ্রেষ্ঠ প্যারেড কমান্ডার",
            nameEn = "Best Central Parade Commander",
            category = "Parade",
            dayNo = 5,
            maxScore = 100,
            judgeName = "এয়ার কমোডোর মো: শওকত আলী (অব:)",
            status = "SCHEDULED",
            prizeCount = 1,
            isTeamEvent = false,
            selectionScope = "CAMP"
        ),
        CompetitionEntity(
            id = 14,
            nameBn = "শ্রেষ্ঠ রেজিমেন্ট কমান্ডার (২৪ সাব-গ্রুপ)",
            nameEn = "Best Regiment Commanders (24 Subgroups)",
            category = "Parade",
            dayNo = 5,
            maxScore = 100,
            judgeName = "প্যারেড ও শৃঙ্খলা মূল্যায়ন কমিটি",
            status = "SCHEDULED",
            prizeCount = 24,
            isTeamEvent = false,
            selectionScope = "SUBGROUP"
        ),
        CompetitionEntity(
            id = 15,
            nameBn = "শ্রেষ্ঠ পাইলট (প্যারেড স্কোয়াড)",
            nameEn = "Best Parade Pilots",
            category = "Parade",
            dayNo = 5,
            maxScore = 100,
            judgeName = "প্যারেড মূল্যায়ন কমিটি",
            status = "SCHEDULED",
            prizeCount = 4,
            isTeamEvent = false,
            selectionScope = "CAMP"
        ),
        CompetitionEntity(
            id = 16,
            nameBn = "শ্রেষ্ঠ ড্রামার (প্যারেড ব্যান্ড)",
            nameEn = "Best Parade Drummers",
            category = "Parade",
            dayNo = 5,
            maxScore = 100,
            judgeName = "প্যারেড মূল্যায়ন কমিটি",
            status = "SCHEDULED",
            prizeCount = 4,
            isTeamEvent = false,
            selectionScope = "CAMP"
        ),
        CompetitionEntity(
            id = 17,
            nameBn = "শ্রেষ্ঠ ডেলিগেট (৬ গ্রুপে সেরা ১ জন করে)",
            nameEn = "Best Camp Delegates (1 per Group)",
            category = "Leadership",
            dayNo = 5,
            maxScore = 100,
            judgeName = "মোহাম্মদ রেজাউল করিম ও মূল্যায়ন বিভাগ",
            status = "SCHEDULED",
            prizeCount = 6,
            isTeamEvent = false,
            selectionScope = "GROUP"
        ),
        CompetitionEntity(
            id = 18,
            nameBn = "শ্রেষ্ঠ গ্রুপ চ্যাম্পিয়ন ট্রফি (দলীয়)",
            nameEn = "Overall Champion Group Trophy (Team)",
            category = "Team",
            dayNo = 5,
            maxScore = 100,
            judgeName = "ড. মুহাম্মদ রেদওয়ানুর রহমান ও ক্যাম্প চিফ",
            status = "SCHEDULED",
            prizeCount = 1,
            isTeamEvent = true,
            selectionScope = "CAMP"
        ),
        CompetitionEntity(
            id = 19,
            nameBn = "শ্রেষ্ঠ সাব-গ্রুপ পুরস্কার (৬ গ্রুপে ১টি করে)",
            nameEn = "Best Subgroup Award (1 per Group)",
            category = "Team",
            dayNo = 5,
            maxScore = 100,
            judgeName = "মোহাম্মদ রেজাউল করিম ও মূল্যায়ন বিভাগ",
            status = "SCHEDULED",
            prizeCount = 6,
            isTeamEvent = true,
            selectionScope = "GROUP"
        ),
        CompetitionEntity(
            id = 20,
            nameBn = "সাংস্কৃতিক সন্ধ্যা শ্রেষ্ঠ দলীয় পরিবেশনা",
            nameEn = "Cultural Night Best Team Performance",
            category = "Arts",
            dayNo = 4,
            maxScore = 100,
            judgeName = "মুহাম্মদ শাহাদাত হোসাইন ও সাংস্কৃতিক কমিটি",
            status = "SCHEDULED",
            prizeCount = 3,
            isTeamEvent = true,
            selectionScope = "CAMP"
        ),
        CompetitionEntity(
            id = 21,
            nameBn = "দেয়ালিকা প্রদর্শনী দলীয় পুরস্কার (৬ গ্রুপে ১টি করে)",
            nameEn = "Wall Magazine Team Award (1 per Group)",
            category = "Arts",
            dayNo = 5,
            maxScore = 100,
            judgeName = "আমিনুল ইসলাম বুলবুল ও মিডিয়া বিভাগ",
            status = "SCHEDULED",
            prizeCount = 6,
            isTeamEvent = true,
            selectionScope = "GROUP"
        )
    )

    val judges = listOf(
        JudgeEntity(
            id = 1,
            nameBn = "মুহাম্মদ শাহাদাত হোসাইন",
            nameEn = "Muhammad Shahadat Hossain",
            designation = "ইনচার্জ, স্টেশন-১ ও সাহিত্য-সাংস্কৃতিক প্রতিযোগিতা প্রধান",
            assignedCategory = "Quran",
            phone = "01711-260201",
            notes = "কুরআন, হাদীস, কবিতা আবৃত্তি ও সাংস্কৃতিক প্রতিযোগিতার প্রধান সমন্বয়ক"
        ),
        JudgeEntity(
            id = 2,
            nameBn = "ওমর ফারুক সময়",
            nameEn = "Omar Faruk Somoy",
            designation = "প্রশিক্ষক ও বিচারক, পাবলিক ম্যানারস ও প্রবলেম সলভিং",
            assignedCategory = "Speech",
            phone = "01711-260202",
            notes = "উপস্থিত বক্তৃতা, রোল প্লে ও প্রবলেম সলভিং মূল্যায়নকারী"
        ),
        JudgeEntity(
            id = 3,
            nameBn = "সাইফুল ইসলাম",
            nameEn = "Saiful Islam",
            designation = "মডারেটর, স্টেশন-২ (লাইফ স্কিল ও ক্রাফট)",
            assignedCategory = "Arts",
            phone = "01711-260203",
            notes = "অরিগ্যামী, হ্যান্ড ক্রাফট ও চিত্রাঙ্কন প্রতিযোগিতা বিচারক"
        ),
        JudgeEntity(
            id = 4,
            nameBn = "ইঞ্জিনিয়ার আতিকুর রহমান",
            nameEn = "Engr. Atiqur Rahman",
            designation = "মডারেটর, স্টেশন-৩ ও সাধারণ জ্ঞান কুইজ বিচারক",
            assignedCategory = "Quiz",
            phone = "01711-260204",
            notes = "সাধারণ জ্ঞান ও আউটিং জ্ঞান অন্বেষণ কুইজ জুরি"
        ),
        JudgeEntity(
            id = 5,
            nameBn = "এ. এম. ইউসুফ",
            nameEn = "A. M. Yusuf",
            designation = "মডারেটর, স্টেশন-৪ (ফিকাহ ও সালাত ওয়ার্কশপ)",
            assignedCategory = "Hadith",
            phone = "01711-260205",
            notes = "হাদীস পাঠ, মাসনূন দোয়া ও সালাত অনুশীলন বিচারক"
        ),
        JudgeEntity(
            id = 6,
            nameBn = "মোহাম্মদ রেজাউল করিম",
            nameEn = "Mohammad Rezaul Karim",
            designation = "প্রধান, মূল্যায়ন ও পর্যবেক্ষণ বিভাগ",
            assignedCategory = "Leadership",
            phone = "01711-260206",
            notes = "শ্রেষ্ঠ গ্রুপ, শ্রেষ্ঠ সাব-গ্রুপ, প্যারেড ও শ্রেষ্ঠ ডেলিগেট চূড়ান্ত মূল্যায়নকারী"
        )
    )

    /**
     * All 30 Official Gift Items from Table 21 + Official 835 Competition Award Stock (Table 20).
     */
    val inventoryItems = listOf(
        // --- 30 Official Gift Items for 609 Orphan Students (Table 21) ---
        InventoryItemEntity(id = 1, nameBn = "১. ছেলেদের পোলো শার্ট (২টি করে)", nameEn = "Boys Polo Shirts (2 pcs each)", category = "KIT", openingQty = 730, issuedQty = 730, balanceQty = 0, lowStockThreshold = 20, unit = "টি"),
        InventoryItemEntity(id = 2, nameBn = "২. শীতকালীন জ্যাকেট / উইন্ডব্রেকার", nameEn = "Camp Winter Jacket", category = "KIT", openingQty = 609, issuedQty = 609, balanceQty = 0, lowStockThreshold = 15, unit = "টি"),
        InventoryItemEntity(id = 3, nameBn = "৩. ক্যাম্প লোগো ক্যাপ (ছেলেদের)", nameEn = "Official Logo Cap", category = "KIT", openingQty = 365, issuedQty = 365, balanceQty = 0, lowStockThreshold = 10, unit = "টি"),
        InventoryItemEntity(id = 4, nameBn = "৪. ছেলেদের ট্রাউজার / প্যান্ট (২টি করে)", nameEn = "Boys Trousers (2 pcs each)", category = "KIT", openingQty = 730, issuedQty = 730, balanceQty = 0, lowStockThreshold = 20, unit = "টি"),
        InventoryItemEntity(id = 5, nameBn = "৫. মেয়েদের স্কার্টসহ খিমার সেট (২টি করে)", nameEn = "Girls Skirt & Khimar Set (2 sets each)", category = "KIT", openingQty = 488, issuedQty = 488, balanceQty = 0, lowStockThreshold = 15, unit = "সেট"),
        InventoryItemEntity(id = 6, nameBn = "৬. কেডস / ক্যাঙ্গারু জুতা", nameEn = "Sneakers / Shoes", category = "KIT", openingQty = 609, issuedQty = 600, balanceQty = 9, lowStockThreshold = 10, unit = "জোড়া"),
        InventoryItemEntity(id = 7, nameBn = "৭. স্যান্ডেল / স্লিপার", nameEn = "Sandals / Slippers", category = "KIT", openingQty = 609, issuedQty = 609, balanceQty = 0, lowStockThreshold = 10, unit = "জোড়া"),
        InventoryItemEntity(id = 8, nameBn = "৮. মোজা (২ জোড়া করে)", nameEn = "Socks (2 pairs each)", category = "KIT", openingQty = 1218, issuedQty = 1218, balanceQty = 0, lowStockThreshold = 25, unit = "জোড়া"),
        InventoryItemEntity(id = 9, nameBn = "৯. তোয়ালে (প্রিমিয়াম কটন)", nameEn = "Cotton Bath Towel", category = "GIFT", openingQty = 609, issuedQty = 609, balanceQty = 0, lowStockThreshold = 10, unit = "টি"),
        InventoryItemEntity(id = 10, nameBn = "১০. গামছা", nameEn = "Traditional Gamcha", category = "GIFT", openingQty = 609, issuedQty = 609, balanceQty = 0, lowStockThreshold = 10, unit = "টি"),
        InventoryItemEntity(id = 11, nameBn = "১১. পানির পট / ফ্লাস্ক বোতল", nameEn = "Water Bottle / Flask", category = "GIFT", openingQty = 609, issuedQty = 609, balanceQty = 0, lowStockThreshold = 10, unit = "টি"),
        InventoryItemEntity(id = 12, nameBn = "১২. টুথব্রাশ ও টুথপেস্ট সেট", nameEn = "Toothbrush & Toothpaste Set", category = "HEALTH", openingQty = 609, issuedQty = 609, balanceQty = 0, lowStockThreshold = 15, unit = "সেট"),
        InventoryItemEntity(id = 13, nameBn = "১৩. খাতা সেট (১০টি করে খাতা)", nameEn = "Exercise Notebooks (10 pcs set)", category = "STATIONERY", openingQty = 609, issuedQty = 595, balanceQty = 14, lowStockThreshold = 15, unit = "সেট"),
        InventoryItemEntity(id = 14, nameBn = "১৪. স্কুল ব্যাকপ্যাক ব্যাগ", nameEn = "Premium School Backpack", category = "GIFT", openingQty = 609, issuedQty = 609, balanceQty = 0, lowStockThreshold = 10, unit = "টি"),
        InventoryItemEntity(id = 15, nameBn = "১৫. ট্রাভেল / ক্যারিং ব্যাগ", nameEn = "Large Carrying Bag", category = "GIFT", openingQty = 609, issuedQty = 590, balanceQty = 19, lowStockThreshold = 15, unit = "টি"),
        InventoryItemEntity(id = 16, nameBn = "১৬. শিক্ষা উপকরণ বক্স (কলম, পেন্সিল, জ্যামিতি বক্স)", nameEn = "Stationery & Geometry Set", category = "STATIONERY", openingQty = 609, issuedQty = 609, balanceQty = 0, lowStockThreshold = 15, unit = "সেট"),
        InventoryItemEntity(id = 17, nameBn = "১৭. ক্যাম্পিং বেডশিট ও বালিশ সেট", nameEn = "Camping Bed & Pillow Set", category = "GIFT", openingQty = 609, issuedQty = 580, balanceQty = 29, lowStockThreshold = 20, unit = "সেট"),
        InventoryItemEntity(id = 18, nameBn = "১৮. ফ্যামিলি সাইজ মশারি", nameEn = "Family Mosquito Net", category = "GIFT", openingQty = 609, issuedQty = 580, balanceQty = 29, lowStockThreshold = 20, unit = "টি"),
        InventoryItemEntity(id = 19, nameBn = "১৯. রিস্ট ওয়াচ (হাতঘড়ি)", nameEn = "Student Wristwatch", category = "GIFT", openingQty = 609, issuedQty = 590, balanceQty = 19, lowStockThreshold = 15, unit = "টি"),
        InventoryItemEntity(id = 20, nameBn = "২০. চকলেট ও ড্রাই ফুড গিফট বক্স", nameEn = "Chocolate Gift Box", category = "GIFT", openingQty = 609, issuedQty = 575, balanceQty = 34, lowStockThreshold = 20, unit = "বক্স"),
        InventoryItemEntity(id = 21, nameBn = "২১. পার্সোনাল হাইজিন প্যাক (সাবান, শ্যাম্পু, তেল, ভ্যাসলিন)", nameEn = "Personal Hygiene Care Pack", category = "HEALTH", openingQty = 609, issuedQty = 609, balanceQty = 0, lowStockThreshold = 15, unit = "প্যাক"),
        InventoryItemEntity(id = 22, nameBn = "২২. জায়নামায (উন্নত মানের)", nameEn = "Premium Prayer Mat (Janamaz)", category = "GIFT", openingQty = 609, issuedQty = 609, balanceQty = 0, lowStockThreshold = 15, unit = "টি"),
        InventoryItemEntity(id = 23, nameBn = "২৩. আতর / পারফিউম", nameEn = "Attar / Halal Perfume", category = "GIFT", openingQty = 609, issuedQty = 600, balanceQty = 9, lowStockThreshold = 15, unit = "টি"),
        InventoryItemEntity(id = 24, nameBn = "২৪. মায়েদের জন্য বিশেষ উপহার চাদর", nameEn = "Special Gift Chador for Mothers", category = "GIFT", openingQty = 609, issuedQty = 540, balanceQty = 69, lowStockThreshold = 30, unit = "টি"),
        InventoryItemEntity(id = 25, nameBn = "২৫. পিভিসি আইডি কার্ড ও ১৮-স্টার সনদপত্র ফোল্ডার", nameEn = "ID Card & 18-Star Certificate Folder", category = "STATIONERY", openingQty = 609, issuedQty = 609, balanceQty = 0, lowStockThreshold = 10, unit = "সেট"),
        InventoryItemEntity(id = 26, nameBn = "২৬. ক্যাম্প যৌক্তিকতা নোটপ্যাড ও ডায়েরি", nameEn = "Camp Rationale Notepad & Diary", category = "STATIONERY", openingQty = 609, issuedQty = 609, balanceQty = 0, lowStockThreshold = 10, unit = "টি"),
        InventoryItemEntity(id = 27, nameBn = "২৭. ক্যাম্প লোগো ছাতা", nameEn = "WAMY LSC Logo Umbrella", category = "GIFT", openingQty = 609, issuedQty = 585, balanceQty = 24, lowStockThreshold = 15, unit = "টি"),
        InventoryItemEntity(id = 28, nameBn = "২৮. ক্যাম্প স্মারক লোগো মগ", nameEn = "Commemorative Logo Mug", category = "GIFT", openingQty = 609, issuedQty = 585, balanceQty = 24, lowStockThreshold = 15, unit = "টি"),
        InventoryItemEntity(id = 29, nameBn = "২৯. ফুড-গ্রেড টিফিন বক্স", nameEn = "Food-Grade Tiffin Box", category = "GIFT", openingQty = 609, issuedQty = 600, balanceQty = 9, lowStockThreshold = 15, unit = "টি"),
        InventoryItemEntity(id = 30, nameBn = "৩০. পার্সোনাল ফার্স্ট এইড বক্স", nameEn = "Personal First Aid Box", category = "HEALTH", openingQty = 609, issuedQty = 600, balanceQty = 9, lowStockThreshold = 15, unit = "বক্স"),

        // --- Official 835 Competition Prizes Stock (Table 20) ---
        InventoryItemEntity(id = 31, nameBn = "সাব-গ্রুপ ইনডোর ও সৃজনশীল পুরস্কার (৮ ইভেন্ট × ৭২)", nameEn = "Subgroup Indoor Competition Prizes", category = "AWARD", openingQty = 576, issuedQty = 72, balanceQty = 504, lowStockThreshold = 50, unit = "টি"),
        InventoryItemEntity(id = 32, nameBn = "আউটডোর গেমস ও ক্রীড়া পুরস্কার (২ ইভেন্ট × ৭২)", nameEn = "Outdoor Games Medals & Prizes", category = "AWARD", openingQty = 144, issuedQty = 24, balanceQty = 120, lowStockThreshold = 20, unit = "টি"),
        InventoryItemEntity(id = 33, nameBn = "রোল প্লে, রিফ্রেকটিভ ও আউটিং কুইজ পুরস্কার", nameEn = "Role Play, Reflective & Outing Quest Prizes", category = "AWARD", openingQty = 60, issuedQty = 0, balanceQty = 60, lowStockThreshold = 10, unit = "টি"),
        InventoryItemEntity(id = 34, nameBn = "প্যারেড কমান্ডার, রেজিমেন্ট কমান্ডার, পাইলট ও ড্রামার ক্রেস্ট", nameEn = "Parade & Regiment Honor Crests", category = "AWARD", openingQty = 33, issuedQty = 0, balanceQty = 33, lowStockThreshold = 5, unit = "টি"),
        InventoryItemEntity(id = 35, nameBn = "শ্রেষ্ঠ ডেলিগেট, শ্রেষ্ঠ গ্রুপ, সাব-গ্রুপ, সাংস্কৃতিক ও দেয়ালিকা ট্রফি", nameEn = "Grand Champion, Best Delegate & Team Trophies", category = "AWARD", openingQty = 22, issuedQty = 0, balanceQty = 22, lowStockThreshold = 5, unit = "টি")
    )

    val bulletins = listOf(
        BulletinEntity(
            id = 1,
            dayNo = 1,
            dateStr = "২২ অক্টোবর ২০২৬ (১ম দিন)",
            titleBn = "রাঙামাটি ওয়াটারফ্রন্ট রিসোর্টে ৬০৯ এতিম শিক্ষার্থীর মিলনমেলা: শুরু হলো লাইফ স্কিলস ক্যাম্পিং ২০২৬",
            titleEn = "609 Students Gather at Rangamati Waterfront Resort: LSC 2026 Begins",
            summaryBn = "ওয়ামী বাংলাদেশ (WAMY Bangladesh)-এর আয়োজনে গাজীপুরের সফিপুরস্থ রাঙামাটি ওয়াটারফ্রন্ট রিসোর্টে শুরু হলো ৫ দিনব্যাপী লাইফ স্কিলস ক্যাম্পিং ২০২৬। ‘নিজকে গড়ো, গড়তে শেখাও’ স্লোগানে ৬টি গ্রুপ ও ২৪টি সাব-গ্রুপে বিভক্ত হয়ে শিক্ষার্থীরা তাদের আবাসন ও উপহার কিট গ্রহণ করেছে।",
            summaryEn = "Organized by WAMY Bangladesh, the 5-day Life Skills Camping 2026 kicked off at Rangamati Waterfront Resort with 609 orphan students across 6 groups and 24 subgroups.",
            author = "আমিনুল ইসলাম বুলবুল (সম্পাদক, ক্যাম্প বুলেটিন)",
            groupSpotlight = "ঢাকা ড্রিমহোমস, উম্মে সা’দ ও সালাহউদ্দিন আয়ুবী গ্রুপ আজ ১ম সাংস্কৃতিক সন্ধ্যায় চমৎকার পরিবেশনা উপহার দিয়েছে।",
            likesCount = 92
        ),
        BulletinEntity(
            id = 2,
            dayNo = 2,
            dateStr = "২৩ অক্টোবর ২০২৬ (২য় দিন)",
            titleBn = "৬ স্টেশনে প্রাণবন্ত রোটেশন, ফায়ার ড্রিল ও মেডিকেল চেকআপে মুখরিত ক্যাম্পাস",
            titleEn = "Vibrant 6-Station Rotation, Fire Drill & Medical Checkup Across Campus",
            summaryBn = "সকাল ০৯:০০ থেকে দুপুর ০১:৩০ পর্যন্ত ৪৫ মিনিটের ৬টি স্লটে বুখারী, শাফে’য়ী, সালাহউদ্দিন আয়ুবী, সুমাইয়া, উম্মে আইমান ও উম্মে সা’দ গ্রুপ স্টেশন ১ থেকে ৬ সম্পন্ন করেছে। স্টেশন ম্যানেজার আবদুল মোহাইমিন নাঈমের তত্ত্বাবধানে শতভাগ সময়নিষ্ঠতা বজায় ছিল।",
            summaryEn = "All 6 groups rotated smoothly through Stations 1–6 in six 45-minute slots, followed by Jumu'ah prayer, room inspection, hiking, and outdoor games.",
            author = "আমিনুল ইসলাম বুলবুল (মিডিয়া ও প্রকাশনা বিভাগ)",
            groupSpotlight = "রুম পরিদর্শনে প্লাটিনাম-১ ও গোল্ডেন-২ কটেজ সর্বোচ্চ পরিচ্ছন্নতা স্কোর অর্জন করেছে।",
            likesCount = 84
        ),
        BulletinEntity(
            id = 3,
            dayNo = 3,
            dateStr = "২৪ অক্টোবর ২০২৬ (৩য় দিন - শিক্ষা সফর)",
            titleBn = "চিড়িয়াখানা, সামরিক জাদুঘর ও মেট্রোরেল ভ্রমণে উচ্ছ্বসিত ৬০৯ শিক্ষার্থী",
            titleEn = "609 Campers Thrilled by National Zoo, Military Museum & Metro Rail Ride",
            summaryBn = "১৬টি সুসজ্জিত বাসে কঠোর নিরাপত্তা ও সাব-গ্রুপ রোল-কলের মধ্য দিয়ে শিক্ষার্থীরা জাতীয় চিড়িয়াখানা, সংসদ ভবন, বায়তুল মোকাররম, সামরিক জাদুঘর এবং স্বপ্নের মেট্রোরেল ভ্রমণ সম্পন্ন করেছে।",
            summaryEn = "Students visited National Zoo, Parliament, Baitul Mukarram, Military Museum, and rode the Dhaka Metro Rail with 100% safe headcount compliance.",
            author = "মিডিয়া ও ডকুমেন্টেশন টিম",
            groupSpotlight = "আউটিং জ্ঞান অন্বেষণ কুইজে ২৪টি সাব-গ্রুপ থেকেই বিজয়ী নির্বাচিত হয়েছে।",
            likesCount = 115
        )
    )

    val emergencyContacts = listOf(
        EmergencyContact(
            roleTitleBn = "ক্যাম্প প্রধান (Camp Chief)",
            roleTitleEn = "Camp Chief",
            officerName = "এয়ার কমোডোর মো: শওকত আলী (অব:)",
            phone = "01711-260001",
            location = "কেন্দ্রীয় কন্ট্রোল রুম, রাঙামাটি রিসোর্ট"
        ),
        EmergencyContact(
            roleTitleBn = "প্রোগ্রাম চেয়ারম্যান ও কান্ট্রি ডিরেক্টর",
            roleTitleEn = "Country Director, WAMY Bangladesh",
            officerName = "ড. মুহাম্মদ রেদওয়ানুর রহমান",
            phone = "01711-260002",
            location = "ভিআইপি কমান্ড স্যুইট (Golden-7)"
        ),
        EmergencyContact(
            roleTitleBn = "স্টেশন ম্যানেজার (Station Manager)",
            roleTitleEn = "Station Manager",
            officerName = "আবদুল মোহাইমিন নাঈম",
            phone = "01711-260003",
            location = "স্টেশন কন্ট্রোল ডেস্ক (স্টেশন ১–৬)"
        ),
        EmergencyContact(
            roleTitleBn = "মেডিকেল, ডেন্টাল ও আই ক্যাম্প প্রধান",
            roleTitleEn = "Medical & Dental Camp Lead",
            officerName = "ডা. ওরাকাতুল জান্নাত ও মোস্তাফিজুর রহমান গাজী",
            phone = "01711-260006",
            location = "স্টেশন-৬ মেডিকেল বুথ ও অ্যাম্বুলেন্স বে"
        ),
        EmergencyContact(
            roleTitleBn = "শৃঙ্খলা ও নিরাপত্তা প্রধান",
            roleTitleEn = "Security & Discipline Lead",
            officerName = "মোহাম্মদ আল আমিন কবির",
            phone = "01711-260007",
            location = "প্রধান ফটক ও নিরাপত্তা কন্ট্রোল রুম"
        ),
        EmergencyContact(
            roleTitleBn = "আবাসন, পরিবহন ও লজিস্টিকস",
            roleTitleEn = "Accommodation & Transport Leads",
            officerName = "আহসান হাবীব / মাহবুবুর রহমান / সাফওয়ানুর রহমান",
            phone = "01711-260008",
            location = "লজিস্টিকস ও পরিবহন ডেস্ক"
        )
    )

    /**
     * All 71 Real Resort Rooms across Platinum 1–6, Golden 1–7, and Rajonigandha/Silver (Tables 50–57).
     */
    val rooms: List<RoomEntity> = OfficialRoster2026.buildOfficialRooms()

    val travelVehicles = listOf(
        TravelVehicleEntity(id = 1, vehicleNo = "বাস-০১ (বুখারী-০১ ও ০২)", driverName = "মো. রফিক মিয়া", driverPhone = "01711-888001", inChargeName = "আয়াতুল্লাহ নাহিদ / ওয়ালী উল্লাহ", inChargePhone = "01711-260101", capacity = 62, assignedGroupId = "bukhari", departureHeadcount = 60, returnHeadcount = 60, status = "SCHEDULED"),
        TravelVehicleEntity(id = 2, vehicleNo = "বাস-০২ (বুখারী-০৩ ও ০৪)", driverName = "মো. বাবুল হোসেন", driverPhone = "01711-888002", inChargeName = "হাফেজ সাখাওয়াত / আবিদ হাসান", inChargePhone = "01711-260101", capacity = 62, assignedGroupId = "bukhari", departureHeadcount = 60, returnHeadcount = 60, status = "SCHEDULED"),
        TravelVehicleEntity(id = 3, vehicleNo = "বাস-০৩ (শাফে’য়ী-০১ ও ০২)", driverName = "মো. সেলিম উদ্দিন", driverPhone = "01711-888003", inChargeName = "সুলতান মাহমুদ / সালাহউদ্দিন", inChargePhone = "01711-260102", capacity = 64, assignedGroupId = "shafeyi", departureHeadcount = 62, returnHeadcount = 62, status = "SCHEDULED"),
        TravelVehicleEntity(id = 4, vehicleNo = "বাস-০৪ (শাফে’য়ী-০৩ ও ০৪)", driverName = "মো. কালাম মিয়া", driverPhone = "01711-888004", inChargeName = "আরিফুল ইসলাম / মহসিন", inChargePhone = "01711-260102", capacity = 64, assignedGroupId = "shafeyi", departureHeadcount = 61, returnHeadcount = 61, status = "SCHEDULED"),
        TravelVehicleEntity(id = 5, vehicleNo = "বাস-০৫ (সালাহউদ্দিন আয়ুবী-০১ ও ০২)", driverName = "মো. খোরশেদ আলম", driverPhone = "01711-888005", inChargeName = "নাসির উদ্দিন / ইব্রাহিম খলিল", inChargePhone = "01711-260103", capacity = 64, assignedGroupId = "salahuddin", departureHeadcount = 61, returnHeadcount = 61, status = "SCHEDULED"),
        TravelVehicleEntity(id = 6, vehicleNo = "বাস-০৬ (সালাহউদ্দিন আয়ুবী-০৩ ও ০৪)", driverName = "মো. ফারুক হাসান", driverPhone = "01711-888006", inChargeName = "আবদুল্লাহ আল মামুন / রাকিবুল", inChargePhone = "01711-260103", capacity = 64, assignedGroupId = "salahuddin", departureHeadcount = 61, returnHeadcount = 61, status = "SCHEDULED"),
        TravelVehicleEntity(id = 7, vehicleNo = "বাস-০৭ (সুমাইয়া-০১..০৪ বালিকা)", driverName = "মো. জসিম উদ্দিন", driverPhone = "01711-888007", inChargeName = "শারমিন সুলতানা ও নারী মেন্টর টিম", inChargePhone = "01711-260104", capacity = 86, assignedGroupId = "sumaiya", departureHeadcount = 84, returnHeadcount = 84, status = "SCHEDULED"),
        TravelVehicleEntity(id = 8, vehicleNo = "বাস-০৮ (উম্মে আইমান-০১..০৪ বালিকা)", driverName = "মো. আনোয়ার হোসেন", driverPhone = "01711-888008", inChargeName = "তাহমিনা বিনতে নূর ও নারী মেন্টর টিম", inChargePhone = "01711-260105", capacity = 90, assignedGroupId = "umm_ayman", departureHeadcount = 89, returnHeadcount = 89, status = "SCHEDULED"),
        TravelVehicleEntity(id = 9, vehicleNo = "বাস-০৯ (উম্মে সা’দ-০১..০৪ বালিকা)", driverName = "মো. মনিরুল ইসলাম", driverPhone = "01711-888009", inChargeName = "হামিদা আক্তার ও নারী মেন্টর টিম", inChargePhone = "01711-260106", capacity = 75, assignedGroupId = "umm_saad", departureHeadcount = 71, returnHeadcount = 71, status = "SCHEDULED")
    )

    val dutyRosters = listOf(
        DutyRosterEntity(id = 1, committeeKey = "COMMAND", committeeNameBn = "ক্যাম্প কমান্ড ও সমন্বয়", taskTitle = "সার্বিক ক্যাম্প পরিচালনা ও আন্তঃবিভাগ সমন্বয়", assignedPersonName = "এয়ার কমোডোর মো: শওকত আলী (অব:) ও ওয়াহিদুজ্জামান মাসুদ", assignedPersonPhone = "01711-260001", shiftTime = "সার্বক্ষণিক", location = "সেন্ট্রাল কন্ট্রোল রুম", status = "IN_PROGRESS"),
        DutyRosterEntity(id = 2, committeeKey = "STATION_MANAGEMENT", committeeNameBn = "স্টেশন ব্যবস্থাপনা বিভাগ", taskTitle = "৬টি স্টেশনের ৪৫ মিনিট রোটেশন, ঘণ্টা ও ১৮-স্টার সিল তদারকি", assignedPersonName = "আবদুল মোহাইমিন নাঈম (স্টেশন ম্যানেজার)", assignedPersonPhone = "01711-260003", shiftTime = "সকাল ০৮:৪৫ - দুপুর ০১:৩০", location = "স্টেশন জোন ১–৬", status = "IN_PROGRESS"),
        DutyRosterEntity(id = 3, committeeKey = "DISCIPLINE", committeeNameBn = "শৃঙ্খলা, প্যারেড ও নিরাপত্তা", taskTitle = "প্যারেড অনুশীলন, গেট কন্ট্রোল ও ফায়ার সেফটি তদারকি", assignedPersonName = "মোহাম্মদ আল আমিন কবির", assignedPersonPhone = "01711-260007", shiftTime = "সার্বক্ষণিক (২৪ ঘণ্টা)", location = "প্রধান মাঠ ও ফটক", status = "IN_PROGRESS"),
        DutyRosterEntity(id = 4, committeeKey = "DINING", committeeNameBn = "খাদ্য ও পিঠা উৎসব বিভাগ", taskTitle = "৩ বেলা খাবার, নাস্তা ও ৫ম দিনের পিঠা উৎসব ব্যবস্থাপনা", assignedPersonName = "মোহাম্মদ কাউসার ও নাজমুল হাসান", assignedPersonPhone = "01711-260009", shiftTime = "সকাল ০৬:০০ - রাত ১০:০০", location = "কেন্দ্রীয় ডাইনিং হল", status = "IN_PROGRESS"),
        DutyRosterEntity(id = 5, committeeKey = "MEDICAL", committeeNameBn = "মেডিকেল, ডেন্টাল ও আই ক্যাম্প", taskTitle = "স্টেশন-৬ চেকআপ, ডেন্টাল বাস ও ২৪/৭ জরুরি চিকিৎসা সেবা", assignedPersonName = "ডা. ওরাকাতুল জান্নাত ও মোস্তাফিজুর রহমান গাজী", assignedPersonPhone = "01711-260006", shiftTime = "২৪ ঘণ্টা জরুরি + স্টেশন স্লট", location = "স্টেশন-৬ ও মেডিকেল বুথ", status = "IN_PROGRESS"),
        DutyRosterEntity(id = 6, committeeKey = "EVALUATION", committeeNameBn = "মূল্যায়ন ও পর্যবেক্ষণ বিভাগ", taskTitle = "১১৪টি সেশন সময়নিষ্ঠতা ট্র্যাকিং, রুম পরিদর্শন ও সান্ধ্যকালীন ফিডব্যাক", assignedPersonName = "মোহাম্মদ রেজাউল করিম", assignedPersonPhone = "01711-260206", shiftTime = "প্রতি সেশন ও রাত ০৯:৩০", location = "মূল্যায়ন সেল", status = "IN_PROGRESS"),
        DutyRosterEntity(id = 7, committeeKey = "GIFTS_PRIZES", committeeNameBn = "উপহার (৩০ আইটেম) ও পুরস্কার (৮৩৫টি)", taskTitle = "উপহার কিট, মায়েদের চাদর ও সেশনভিত্তিক পুরস্কার বিতরণ", assignedPersonName = "সাফওয়ানুর রহমান ও আতাউর রহমান", assignedPersonPhone = "01711-260008", shiftTime = "সকাল ০৯:০০ - সন্ধ্যা ০৭:০০", location = "গিফট ও প্রাইজ স্টোর", status = "IN_PROGRESS"),
        DutyRosterEntity(id = 8, committeeKey = "OUTING_TRANSPORT", committeeNameBn = "শিক্ষা সফর ও পরিবহন বিভাগ", taskTitle = "ঢাকা সফর, মেট্রোরেল ও নন্দন পার্ক ভ্রমণ বাস ও হেডকাউন্ট", assignedPersonName = "মাহবুবুর রহমান ও তারিকুল ইসলাম", assignedPersonPhone = "01711-260010", shiftTime = "৩য় ও ৪র্থ দিন", location = "বাস বহর ও মেট্রোরেল", status = "PENDING"),
        DutyRosterEntity(id = 9, committeeKey = "MEDIA_BULLETIN", committeeNameBn = "মিডিয়া, দেয়ালিকা ও ক্যাম্প বুলেটিন", taskTitle = "দৈনিক বুলেটিন প্রকাশ, ২৪ দেয়ালিকা প্রদর্শনী ও ফটো ডকুমেন্টেশন", assignedPersonName = "আমিনুল ইসলাম বুলবুল", assignedPersonPhone = "01711-260011", shiftTime = "দুপুর ০১:৩০ ও রাত ০৮:০০", location = "মিডিয়া ও প্রকাশনা ডেস্ক", status = "IN_PROGRESS")
    )

    val campGoals = listOf(
        CampGoalMetricEntity(id = 1, metricTitleBn = "৬০৯ জন এতিম শিক্ষার্থীর শতভাগ উপস্থিতি ও নিরাপদ আবাসন", targetValue = 100.0, currentValue = 99.5, unit = "%", category = "হাজিরা"),
        CampGoalMetricEntity(id = 2, metricTitleBn = "১১৪টি ক্যাম্প সেশন নির্ধারিত সময়ে শুরু ও সমাপ্তির হার", targetValue = 100.0, currentValue = 96.5, unit = "%", category = "সময়নিষ্ঠতা"),
        CampGoalMetricEntity(id = 3, metricTitleBn = "৬ স্টেশন × ৩ দিন = ১৮-স্টার সিল অর্জন অগ্রগতি", targetValue = 100.0, currentValue = 94.0, unit = "%", category = "স্টেশন"),
        CampGoalMetricEntity(id = 4, metricTitleBn = "মেডিকেল, ডেন্টাল বাস ও আই ক্যাম্প স্ক্রিনিং কভারেজ", targetValue = 100.0, currentValue = 91.8, unit = "%", category = "স্বাস্থ্য"),
        CampGoalMetricEntity(id = 5, metricTitleBn = "ঢাকা মেট্রোরেল ও নন্দন পার্ক সফর জিরো-লস্ট-চাইল্ড হেডকাউন্ট", targetValue = 100.0, currentValue = 100.0, unit = "%", category = "নিরাপত্তা"),
        CampGoalMetricEntity(id = 6, metricTitleBn = "৩০টি উপহার, মায়েদের চাদর ও ৮৩৫টি পুরস্কার নির্ভুল বিতরণ", targetValue = 100.0, currentValue = 97.2, unit = "%", category = "লজিস্টিকস")
    )

    val staffAccounts = listOf(
        UserAccountEntity(
            email = "admin@lsc.org",
            fullName = "সুপার অ্যাডমিন (WAMY IT ও কন্ট্রোল রুম)",
            role = "SUPER_ADMIN",
            passwordHash = com.example.data.repository.CampRepository.hashPasswordWithSalt("Lsc@Admin2026"),
            phone = "01711-260000"
        ),
        UserAccountEntity(
            email = "chief@lsc.org",
            fullName = "এয়ার কমোডোর মো: শওকত আলী (অব:) — ক্যাম্প প্রধান",
            role = "CAMP_CHIEF",
            passwordHash = com.example.data.repository.CampRepository.hashPasswordWithSalt("Lsc@Chief2026"),
            phone = "01711-260001"
        ),
        UserAccountEntity(
            email = "stationmanager@lsc.org",
            fullName = "আবদুল মোহাইমিন নাঈম — স্টেশন ম্যানেজার",
            role = "STATION_MANAGER",
            passwordHash = com.example.data.repository.CampRepository.hashPasswordWithSalt("Lsc@Station2026"),
            phone = "01711-260003"
        ),
        UserAccountEntity(
            email = "station@lsc.org",
            fullName = "সাইফুল ইসলাম — স্টেশন মডারেটর (লাইফ স্কিল)",
            role = "STATION_LEADER",
            passwordHash = com.example.data.repository.CampRepository.hashPasswordWithSalt("Lsc@Mod2026"),
            phone = "01711-260203"
        ),
        UserAccountEntity(
            email = "leader@lsc.org",
            fullName = "আয়াতুল্লাহ নাহিদ — গ্রুপ লিডার (বুখারী)",
            role = "GROUP_LEADER",
            passwordHash = com.example.data.repository.CampRepository.hashPasswordWithSalt("Lsc@Lead2026"),
            phone = "01711-260101"
        ),
        UserAccountEntity(
            email = "medical@lsc.org",
            fullName = "ডা. ওরাকাতুল জান্নাত — মেডিকেল ও ডেন্টাল ইনচার্জ",
            role = "MEDICAL",
            passwordHash = com.example.data.repository.CampRepository.hashPasswordWithSalt("Lsc@Med2026"),
            phone = "01711-260006"
        ),
        UserAccountEntity(
            email = "judge@lsc.org",
            fullName = "মুহাম্মদ শাহাদাত হোসাইন — প্রধান বিচারক ও মূল্যায়ন",
            role = "COMPETITION_JUDGE",
            passwordHash = com.example.data.repository.CampRepository.hashPasswordWithSalt("Lsc@Judge2026"),
            phone = "01711-260201"
        ),
        UserAccountEntity(
            email = "logistics@lsc.org",
            fullName = "সাফওয়ানুর রহমান — উপহার ও লজিস্টিকস প্রধান",
            role = "INVENTORY_OFFICER",
            passwordHash = com.example.data.repository.CampRepository.hashPasswordWithSalt("Lsc@Log2026"),
            phone = "01711-260008"
        ),
        UserAccountEntity(
            email = "reg@lsc.org",
            fullName = "মুহাম্মদ ওয়াহিদুজ্জামান মাসুদ — ক্যাম্প সমন্বয়ক ও রেজিস্ট্রেশন",
            role = "VOLUNTEER",
            passwordHash = com.example.data.repository.CampRepository.hashPasswordWithSalt("Lsc@Reg2026"),
            phone = "01711-260004"
        ),
        UserAccountEntity(
            email = "bulletin@lsc.org",
            fullName = "আমিনুল ইসলাম বুলবুল — সম্পাদক, ক্যাম্প বুলেটিন ও মিডিয়া",
            role = "BULLETIN_EDITOR",
            passwordHash = com.example.data.repository.CampRepository.hashPasswordWithSalt("Lsc@Media2026"),
            phone = "01711-260011"
        )
    )

    val volunteerShifts = listOf(
        VolunteerShiftEntity(
            id = 1,
            volunteerId = 101,
            volunteerName = "আহসান হাবীব ও আবাসন টিম",
            volunteerRole = "আবাসন ও রুম বরাদ্দ ইনচার্জ",
            taskTitleBn = "প্লাটিনাম, গোল্ডেন ও রজনীগন্ধা কটেজে ৬০৯ জনের রুম চেক-ইন",
            taskTitleEn = "Resort Room Check-In Across Platinum, Golden & Rajonigandha",
            departmentOrLocation = "রাঙামাটি ওয়াটারফ্রন্ট রিসোর্ট আবাসন জোন",
            dayNo = 1,
            startTime = "সকাল ০৭:০০",
            endTime = "দুপুর ০১:০০",
            taskDescription = "২৪টি সাব-গ্রুপের শিক্ষার্থীদের নির্ধারিত ৭১টি কক্ষে বেড বরাদ্দ ও ৩০টি উপহার সামগ্রী বুঝিয়ে দেওয়া।",
            status = "CONFIRMED",
            confirmedAt = System.currentTimeMillis() - 3600000L
        ),
        VolunteerShiftEntity(
            id = 2,
            volunteerId = 102,
            volunteerName = "আবদুল মোহাইমিন নাঈম ও স্টেশন টিম",
            volunteerRole = "স্টেশন ম্যানেজমেন্ট ও টাইমকিপার",
            taskTitleBn = "সকাল ০৯:০০–০১:৩০ পর্যন্ত ৬টি ৪৫-মিনিট স্লট রোটেশন ও ঘণ্টা তদারকি",
            taskTitleEn = "Morning 6x45m Station Rotation Bell & Seal Tracking",
            departmentOrLocation = "স্টেশন জোন ১–৬",
            dayNo = 2,
            startTime = "সকাল ০৮:৪৫",
            endTime = "দুপুর ০১:৩০",
            taskDescription = "প্রতি ৪৫ মিনিট অন্তর রোটেশন বেল বাজানো, সাব-গ্রুপ ব্যাচ হাজিরা ও ১৮-স্টার সিল নিশ্চিত করা।",
            status = "CONFIRMED",
            confirmedAt = System.currentTimeMillis() - 1800000L
        ),
        VolunteerShiftEntity(
            id = 3,
            volunteerId = 103,
            volunteerName = "মাহবুবুর রহমান ও তারিকুল ইসলাম",
            volunteerRole = "ঢাকা শিক্ষা সফর ও মেট্রোরেল ক্রু",
            taskTitleBn = "চিড়িয়াখানা, সামরিক জাদুঘর ও মেট্রোরেল ভ্রমণে সাব-গ্রুপ চেকপয়েন্ট হেডকাউন্ট",
            taskTitleEn = "Dhaka Tour & Metro Rail Zero-Lost-Child Checkpoint Headcount",
            departmentOrLocation = "১৬টি বাস বহর ও মেট্রোরেল স্টেশন",
            dayNo = 3,
            startTime = "সকাল ০৬:০০",
            endTime = "রাত ০৯:০০",
            taskDescription = "প্রতিটি স্পটে ওঠা ও নামার সময় ২৪ জন সাব-গ্রুপ ইনচার্জের মাধ্যমে ৬০৯ শিক্ষার্থীর হেডকাউন্ট নিশ্চিতকরণ।",
            status = "SCHEDULED"
        ),
        VolunteerShiftEntity(
            id = 4,
            volunteerId = 104,
            volunteerName = "শফিকুল ইসলাম বাঁধন ও কাজী নজরুল ইসলাম",
            volunteerRole = "ভ্রাম্যমাণ ডেন্টাল বাস ও আই ক্যাম্প সমন্বয়ক",
            taskTitleBn = "স্টেশন-৬ ডেন্টাল চেকআপ ও চক্ষু পরীক্ষা কিউ ম্যানেজমেন্ট",
            taskTitleEn = "Mobile Dental Bus & Eye Camp Queue Coordination",
            departmentOrLocation = "স্টেশন-৬ মেডিকেল ও ডেন্টাল বাস জোন",
            dayNo = 4,
            startTime = "সকাল ০৯:০০",
            endTime = "দুপুর ০১:৩০",
            taskDescription = "দন্ত ও চক্ষু পরীক্ষায় আগত গ্রুপের সিরিয়াল বজায় রাখা, ফটো ডকুমেন্টেশন ও লিফলেট বিতরণ।",
            status = "SCHEDULED"
        ),
        VolunteerShiftEntity(
            id = 5,
            volunteerId = 105,
            volunteerName = "মোহাম্মদ কাউসার ও নাজমুল হাসান",
            volunteerRole = "পিঠা উৎসব ও সমাপনী আপ্যায়ন টিম",
            taskTitleBn = "৫ম দিনের ঐতিহ্যবাহী পিঠা উৎসব স্টল ও সমাপনী ডিনার পরিচালনা",
            taskTitleEn = "Day 5 Traditional Pitha Utshab & Closing Feast Operations",
            departmentOrLocation = "লেকসাইড পিঠা উৎসব চত্বর",
            dayNo = 5,
            startTime = "বিকাল ০৪:০০",
            endTime = "রাত ০৮:৩০",
            taskDescription = "৬টি গ্রুপের জন্য সুশৃঙ্খল পিঠা স্টল পরিচালনা ও বিদায়ী খাবার প্যাক বিতরণ।",
            status = "SCHEDULED"
        )
    )

    fun generateKitDistributions(participants: List<ParticipantEntity>): List<KitDistributionEntity> {
        val boyShirtSizes = listOf("M", "L", "S", "XL", "M")
        val girlKhimarSizes = listOf("40\"", "42\"", "38\"", "44\"", "40\"")
        val shoeSizes = listOf("38", "39", "40", "37", "41", "36")
        return participants.mapIndexed { idx, p ->
            val isGirl = p.gender.equals("Female", ignoreCase = true)
            val clothSize = if (isGirl) {
                "খিমার-স্কার্ট ${girlKhimarSizes[idx % girlKhimarSizes.size]}"
            } else {
                "পোলো-ট্রাউজার ${boyShirtSizes[idx % boyShirtSizes.size]}"
            }
            KitDistributionEntity(
                id = (idx + 1).toLong(),
                participantId = p.id,
                campCode = p.campCode,
                participantName = p.nameBn,
                groupName = p.groupNameBn,
                tShirtSize = boyShirtSizes[idx % boyShirtSizes.size],
                kitBagStatus = if (idx < 590) "CONFIRMED" else "DISTRIBUTED",
                giftPackStatus = if (idx < 575) "CONFIRMED" else "ALLOCATED",
                idBadgeStatus = "CONFIRMED",
                notebookStatus = "CONFIRMED",
                receivedAt = System.currentTimeMillis() - (idx * 30000L),
                deliveredBy = "উপহার বিভাগ (সাফওয়ানুর রহমান)",
                clothingSize = clothSize,
                shoeSize = shoeSizes[idx % shoeSizes.size],
                motherChadorStatus = if (idx < 540) "CONFIRMED" else "ALLOCATED",
                distributedItemsCount = if (idx < 540) 30 else 28
            )
        }
    }
}
