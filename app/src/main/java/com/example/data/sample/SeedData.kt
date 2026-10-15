package com.example.data.sample

import com.example.data.model.*

object SeedData {

    val groups = listOf(
        CampGroupEntity(
            id = "bukhari",
            nameBn = "বুখারী",
            nameEn = "Bukhari",
            leaderName = "হাফেজ তানভীর আহমেদ (Tanvir Ahmed)",
            leaderPhone = "01711-234567",
            colorHex = 0xFF1976D2,
            memberCount = 101,
            totalPoints = 840
        ),
        CampGroupEntity(
            id = "sumaiya",
            nameBn = "সুমাইয়া",
            nameEn = "Sumaiya",
            leaderName = "মুহতারামা খাদিজা আক্তার (Khadija Akter)",
            leaderPhone = "01819-345678",
            colorHex = 0xFFC2185B,
            memberCount = 100,
            totalPoints = 890
        ),
        CampGroupEntity(
            id = "shafeyi",
            nameBn = "শাফেয়ী",
            nameEn = "Shafeyi",
            leaderName = "মাওলানা আব্দুল গাফফার (Abdul Gaffar)",
            leaderPhone = "01912-456789",
            colorHex = 0xFF00796B,
            memberCount = 101,
            totalPoints = 780
        ),
        CampGroupEntity(
            id = "umm_ayman",
            nameBn = "উম্মে আয়মান",
            nameEn = "Umm Ayman",
            leaderName = "মুহতারামা সাবিহা পারভীন (Sabiha Parveen)",
            leaderPhone = "01722-567890",
            colorHex = 0xFF7B1FA2,
            memberCount = 100,
            totalPoints = 815
        ),
        CampGroupEntity(
            id = "salahuddin",
            nameBn = "সালাহউদ্দীন আইয়ুবী",
            nameEn = "Salahuddin Ayyubi",
            leaderName = "ইঞ্জিনিয়ার রায়হান উদ্দিন (Rayhan Uddin)",
            leaderPhone = "01611-678901",
            colorHex = 0xFFE65100,
            memberCount = 101,
            totalPoints = 920
        ),
        CampGroupEntity(
            id = "umm_saad",
            nameBn = "উম্মে সা'দ",
            nameEn = "Umm Sa'ad",
            leaderName = "মুহতারামা নাসরিন জাহান (Nasrin Jahan)",
            leaderPhone = "01511-789012",
            colorHex = 0xFF388E3C,
            memberCount = 100,
            totalPoints = 860
        )
    )

    val stations = listOf(
        StationEntity(
            id = 1,
            nameBn = "স্টেশন ১: প্রতিযোগিতা ও শিষ্টাচার",
            nameEn = "Station 1: Competitions & Etiquette",
            moderatorName = "উস্তাদ মাহমুদুল হাসান (Mahmudul Hasan)",
            location = "অডিটোরিয়াম উইং এ (Auditorium A)",
            objectiveBn = "সুন্নাহসম্মত আদব, দলগত আচরণ, প্রতিযোগিতার নিয়মাবলি ও সুন্দর চরিত্র গঠন",
            iconKey = "quran",
            materialsNeeded = "শিষ্টাচার ম্যানুয়াল, স্কোরশিট, প্রজেক্টর"
        ),
        StationEntity(
            id = 2,
            nameBn = "স্টেশন ২: লাইফ স্কিল ও ব্যবহারিক দক্ষতা",
            nameEn = "Station 2: Life Skills & Practical Wisdom",
            moderatorName = "ব্রাদার তানভীর হোসাইন (Tanvir Hossain)",
            location = "স্কিল ওয়ার্কশপ শেড (Skills Shed)",
            objectiveBn = "বাস্তব জীবনের সিদ্ধান্ত গ্রহণ, দুর্যোগ প্রস্তুতি, সময় ব্যবস্থাপনা ও স্বাবলম্বিতা",
            iconKey = "science",
            materialsNeeded = "টুলকিট, রশি, কম্পাস, ওয়ার্কবুক"
        ),
        StationEntity(
            id = 3,
            nameBn = "স্টেশন ৩: শিশুতোষ আলোচনা ও মানসিক বিকাশ",
            nameEn = "Station 3: Child Development & Psychology",
            moderatorName = "মুহতারামা খাদিজা আক্তার (Khadija Akter)",
            location = "কাউন্সেলিং রুম ও লাইব্রেরি (Library)",
            objectiveBn = "বয়সোপযোগী মানসিক বিকাশ, ইতিবাচক দৃষ্টিভঙ্গি ও সহমর্মিতা চর্চা",
            iconKey = "speech",
            materialsNeeded = "স্টোরিবুক, কালারিং পেপার, চার্ট"
        ),
        StationEntity(
            id = 4,
            nameBn = "স্টেশন ৪: ফিকাহ ও সালাত প্রশিক্ষণ",
            nameEn = "Station 4: Fiqh & Prayer Mastery",
            moderatorName = "মাওলানা আব্দুল গাফফার (Abdul Gaffar)",
            location = "মসজিদ কমপ্লেক্স হল (Mosque Hall)",
            objectiveBn = "সহীহ পদ্ধতিতে অজু, সালাতের রুকনসমূহ, মাসআলা ও বিনম্র একাগ্রতা",
            iconKey = "fitness",
            materialsNeeded = "অজু গাইড, জায়নামাজ, মাসআলা হ্যান্ডআউট"
        ),
        StationEntity(
            id = 5,
            nameBn = "স্টেশন ৫: রিফ্লেক্টিভ সেশন ও আত্মশুদ্ধি",
            nameEn = "Station 5: Reflective Session & Tazkiyah",
            moderatorName = "শায়খ আবু তালহা (Shaykh Abu Talha)",
            location = "গ্রাউন্ড প্যাভিলিয়ন (Pavilion)",
            objectiveBn = "জীবনের লক্ষ্য নির্ধারণ, আত্মপর্যালোচনা, আন্তরিক তওবা ও নিয়মিত কুরআন চর্চা",
            iconKey = "arts",
            materialsNeeded = "রিফ্লেকশন ডায়েরি, পেনসিল, মোটিভেশনাল কার্ড"
        ),
        StationEntity(
            id = 6,
            nameBn = "স্টেশন ৬: মেডিকেল, ডেন্টাল ও আই ক্যাম্প",
            nameEn = "Station 6: Medical, Dental & Eye Clinic",
            moderatorName = "ডা. তারিকুল ইসলাম ও মেডিকেল টিম",
            location = "মেডিকেল সেন্টার (Camp Clinic)",
            objectiveBn = "দাঁত, চোখ ও সাধারণ স্বাস্থ্য পরীক্ষা, বিপি চেকআপ ও ব্যক্তিগত পরিচ্ছন্নতা",
            iconKey = "medical",
            materialsNeeded = "স্টেথোস্কোপ, ডেন্টাল কিট, আই চার্ট, ভিটামিন"
        )
    )

    fun generateParticipants(): List<ParticipantEntity> {
        val list = mutableListOf<ParticipantEntity>()
        val banglaFirstNames = listOf(
            "আব্দুল্লাহ", "মুহাম্মদ", "আহমেদ", "তাহমিদ", "আরাফাত", "সালমান", "সাকিব", "মাহমুদ",
            "হাসান", "হুসাইন", "ইব্রাহিম", "ইউসুফ", "ফাতিমা", "আয়েশা", "মরিয়ম", "নুসরাত",
            "সামিয়া", "তাসনিম", "খাদিজা", "সায়মা", "রাইয়ান", "ফারহান", "তানভীর", "সাদিয়া",
            "মাহির", "তামিম", "মুস্তাকিম", "জুনায়েদ", "আদনান", "নাসিফ", "জাহিদুল", "কাওসার"
        )
        val banglaLastNames = listOf(
            "আল মামুন", "হোসাইন", "রহমান", "ইসলাম", "চৌধুরী", "খন্দকার", "খান", "আলী",
            "মোল্লা", "শিকদার", "আখন্দ", "তালুকদার", "সরকার", "পাটোয়ারী", "বেগম", "জাহান"
        )
        val enFirstNames = listOf(
            "Abdullah", "Muhammad", "Ahmed", "Tahmid", "Arafat", "Salman", "Sakib", "Mahmud",
            "Hasan", "Husain", "Ibrahim", "Yusuf", "Fatima", "Ayesha", "Maryam", "Nusrat",
            "Samiya", "Tasnim", "Khadija", "Sayma", "Raiyan", "Farhan", "Tanvir", "Sadia",
            "Mahir", "Tamim", "Mustakim", "Junaid", "Adnan", "Nasif", "Zahidul", "Kawsar"
        )
        val enLastNames = listOf(
            "Al Mamun", "Hossain", "Rahman", "Islam", "Chowdhury", "Khandaker", "Khan", "Ali",
            "Mollah", "Sikder", "Akhand", "Talukder", "Sarker", "Patwary", "Begum", "Jahan"
        )
        val districts = listOf(
            "ঢাকা" to "Dhaka",
            "চট্টগ্রাম" to "Chattogram",
            "সিলেট" to "Sylhet",
            "রাজশাহী" to "Rajshahi",
            "রংপুর" to "Rangpur",
            "খুলনা" to "Khulna",
            "বরিশাল" to "Barishal",
            "ময়মনসিংহ" to "Mymensingh",
            "কুমিল্লা" to "Cumilla",
            "বগুড়া" to "Bogura",
            "কক্সবাজার" to "Cox's Bazar",
            "ব্রাহ্মণবাড়িয়া" to "Brahmanbaria",
            "ফেনী" to "Feni",
            "দিনাজপুর" to "Dinajpur"
        )
        val groupsList = groups

        for (i in 1..603) {
            val code = "LSC-2026-%03d".format(i)
            val fileNo = "F-%04d".format(1000 + i)
            val fnIdx = (i * 7) % banglaFirstNames.size
            val lnIdx = (i * 11) % banglaLastNames.size
            val isFemale = fnIdx in 12..19 || fnIdx == 23

            val nameBn = "${banglaFirstNames[fnIdx]} ${banglaLastNames[lnIdx]}"
            val nameEn = "${enFirstNames[fnIdx]} ${enLastNames[lnIdx]}"
            val dist = districts[i % districts.size]
            val grp = groupsList[i % groupsList.size]
            val subNo = (i % 10) + 1
            val subgroup = "${grp.nameEn}-$subNo"
            val block = if (isFemale) "Block B" else "Block A"
            val roomNum = 101 + ((i / 6) % 12)
            val roomNo = "$block Room $roomNum"
            val bedNo = "Bed-${(i % 6) + 1}"

            val medFlag = when {
                i % 29 == 0 -> "অ্যাজমা / শ্বাসকষ্ট (Asthma Inhaler)"
                i % 41 == 0 -> "ধুলো অ্যালার্জি (Dust Allergy)"
                i % 53 == 0 -> "চশমা ব্যবহারকারী (Eye Power)"
                else -> null
            }

            val attendance = 88f + ((i * 13) % 12)
            val totalScore = 65.0 + ((i * 17) % 35)

            list.add(
                ParticipantEntity(
                    id = i.toLong(),
                    campCode = code,
                    fileNo = fileNo,
                    nameBn = nameBn,
                    nameEn = nameEn,
                    districtBn = dist.first,
                    districtEn = dist.second,
                    gender = if (isFemale) "Female" else "Male",
                    age = 11 + (i % 6),
                    groupId = grp.id,
                    groupNameBn = grp.nameBn,
                    groupNameEn = grp.nameEn,
                    subgroup = subgroup,
                    roomNo = roomNo,
                    bedNo = bedNo,
                    mealPref = if (i % 31 == 0) "ঝাল কম (Mild/No Spicy)" else "নিয়মিত (Regular)",
                    guardianName = "মো: অভিভাবক রহমান (Guardian)",
                    guardianPhone = "017%02d-%06d".format((i % 89) + 10, 100000 + i),
                    isCheckedIn = true,
                    medicalFlag = medFlag,
                    totalScore = totalScore,
                    attendancePercentage = attendance,
                    photoAvatarSeed = (i % 8) + 1
                )
            )
        }
        return list
    }

    val scheduleEvents = listOf(
        // Day 1
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "04:45 - 05:45",
            titleBn = "ফজর নামাজ, কুরআন তিলাওয়াত ও মাসনূন দোয়া",
            titleEn = "Fajr Prayer, Quran Tilawat & Morning Azkar",
            category = "Jamaat",
            location = "সেন্ট্রাল মসজিদ (Central Mosque)",
            isCompleted = true
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "06:00 - 07:00",
            titleBn = "প্রভাতী শরীরচর্চা ও পিটি ড্রিল",
            titleEn = "Morning Physical Drill & Fitness",
            category = "Physical",
            location = "প্রধান মাঠ (Main Ground)",
            isCompleted = true
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "07:15 - 08:15",
            titleBn = "স্বাস্থ্যকর নাস্তা ও ক্যাম্প প্রস্তুতি",
            titleEn = "Healthy Breakfast & Camp Preparation",
            category = "Meal",
            location = "ডাইনিং হল (Dining Hall)",
            isCompleted = true
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "08:30 - 10:00",
            titleBn = "স্টেশন রোটেশন সেশন - ১ (Station Rotation 1)",
            titleEn = "Station Rotation Session 1",
            category = "Station",
            stationId = 1,
            location = "নির্ধারিত স্টেশন ১-৬ (Assigned Stations 1-6)",
            isCurrent = true,
            rotationJson = "bukhari:1,sumaiya:2,shafeyi:3,umm_ayman:4,salahuddin:5,umm_saad:6"
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "10:15 - 11:45",
            titleBn = "স্টেশন রোটেশন সেশন - ২ (Station Rotation 2)",
            titleEn = "Station Rotation Session 2",
            category = "Station",
            stationId = 2,
            location = "নির্ধারিত স্টেশন ১-৬ (Assigned Stations 1-6)",
            rotationJson = "bukhari:2,sumaiya:3,shafeyi:4,umm_ayman:5,salahuddin:6,umm_saad:1"
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "12:00 - 01:15",
            titleBn = "দলীয় আলোচনা ও জীবন দক্ষতা প্রেজেন্টেশন",
            titleEn = "Group Discussion & Life Skills Presentation",
            category = "Halaqa",
            location = "সেমিনার হল (Seminar Hall)"
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "01:30 - 02:45",
            titleBn = "যোহর নামাজ, মধ্যাহ্নভোজ ও বিশ্রাম",
            titleEn = "Dhuhr Prayer, Lunch & Power Nap",
            category = "Meal",
            location = "ডাইনিং হল ও হোস্টেল রুম"
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "03:15 - 04:30",
            titleBn = "স্টেশন রোটেশন সেশন - ৩ (Station Rotation 3)",
            titleEn = "Station Rotation Session 3",
            category = "Station",
            stationId = 3,
            location = "নির্ধারিত স্টেশন ১-৬",
            rotationJson = "bukhari:3,sumaiya:4,shafeyi:5,umm_ayman:6,salahuddin:1,umm_saad:2"
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "04:45 - 05:45",
            titleBn = "আসর নামাজ ও দলীয় আউটডোর স্পোর্টস",
            titleEn = "Asr Prayer & Group Sports / Obstacle Run",
            category = "Physical",
            location = "সেন্ট্রাল প্লে-গ্রাউন্ড"
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "06:00 - 07:15",
            titleBn = "মাগরিব নামাজ ও সীরাত পাঠ হালাকা",
            titleEn = "Maghrib Prayer & Seerah Study Circle",
            category = "Halaqa",
            location = "মসজিদ কমপ্লেক্স"
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "07:30 - 09:00",
            titleBn = "আন্তঃগ্রুপ ক্বিরাত ও হাদিস মুখস্থ প্রতিযোগিতা",
            titleEn = "Inter-Group Quran & Hadith Competition",
            category = "Competition",
            location = "সেন্ট্রাল অডিটোরিয়াম"
        ),
        ScheduleEventEntity(
            dayNo = 1,
            timeSlot = "09:15 - 10:15",
            titleBn = "রাতের খাবার, এশা নামাজ ও ক্যাম্পফায়ার বুলেটিন",
            titleEn = "Dinner, Isha Prayer & Campfire Bulletin",
            category = "Meal",
            location = "অ্যাম্পিথিয়েটার"
        ),
        // Day 2
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "08:30 - 10:00",
            titleBn = "স্টেশন রোটেশন সেশন - ৪ (বিজ্ঞান ও রোবোটিক্স)",
            titleEn = "Station Rotation Session 4 (Science & Robotics)",
            category = "Station",
            stationId = 4,
            rotationJson = "bukhari:4,sumaiya:5,shafeyi:6,umm_ayman:1,salahuddin:2,umm_saad:3"
        ),
        ScheduleEventEntity(
            dayNo = 2,
            timeSlot = "10:15 - 11:45",
            titleBn = "স্টেশন রোটেশন সেশন - ৫ (আত্মরক্ষা ও টিম ড্রিল)",
            titleEn = "Station Rotation Session 5 (Self-Defense)",
            category = "Station",
            stationId = 5,
            rotationJson = "bukhari:5,sumaiya:6,shafeyi:1,umm_ayman:2,salahuddin:3,umm_saad:4"
        ),
        // Day 3
        ScheduleEventEntity(
            dayNo = 3,
            timeSlot = "08:30 - 10:00",
            titleBn = "স্টেশন রোটেশন সেশন - ৬ (ক্যালিগ্রাফি ও দেয়াল পত্রিকা)",
            titleEn = "Station Rotation Session 6 (Calligraphy & Magazine)",
            category = "Station",
            stationId = 6,
            rotationJson = "bukhari:6,sumaiya:1,shafeyi:2,umm_ayman:3,salahuddin:4,umm_saad:5"
        )
    )

    val attendanceEvents = listOf(
        AttendanceEventEntity(
            id = 1,
            titleBn = "দিন ১ - ফজর জামাত ও তালীমুল কুরআন",
            titleEn = "Day 1 - Fajr & Quran Session",
            dayNo = 1,
            timeLabel = "05:00 AM",
            targetCount = 603,
            presentCount = 582,
            lateCount = 14,
            absentCount = 7,
            isClosed = true
        ),
        AttendanceEventEntity(
            id = 2,
            titleBn = "দিন ১ - স্টেশন সেশন ১ (সকাল)",
            titleEn = "Day 1 - Station Session 1 (Morning)",
            dayNo = 1,
            timeLabel = "08:30 AM",
            targetCount = 603,
            presentCount = 595,
            lateCount = 5,
            absentCount = 3,
            isClosed = false
        ),
        AttendanceEventEntity(
            id = 3,
            titleBn = "দিন ১ - যোহর ও মধ্যাহ্নভোজ চেক-ইন",
            titleEn = "Day 1 - Dhuhr & Lunch Entry",
            dayNo = 1,
            timeLabel = "01:30 PM",
            targetCount = 603,
            presentCount = 0,
            lateCount = 0,
            absentCount = 0,
            isClosed = false
        ),
        AttendanceEventEntity(
            id = 4,
            titleBn = "দিন ১ - সান্ধ্যকালীন কুইজ ও প্রতিযোগিতা",
            titleEn = "Day 1 - Evening Quiz & Competition",
            dayNo = 1,
            timeLabel = "07:30 PM",
            targetCount = 603,
            presentCount = 0,
            lateCount = 0,
            absentCount = 0,
            isClosed = false
        )
    )

    val competitions = listOf(
        CompetitionEntity(
            id = 1,
            nameBn = "হিফজুল হাদিস ও অনুবাদ প্রতিযোগিতা",
            nameEn = "Hadith Memorization & Translation",
            category = "Hadith",
            dayNo = 1,
            maxScore = 100,
            judgeName = "মাওলানা আব্দুল গাফফার (Judge 1)",
            status = "LIVE"
        ),
        CompetitionEntity(
            id = 2,
            nameBn = "ক্বিরাত ও তাজবীদ প্রতিযোগিতা",
            nameEn = "Quran Recitation & Tajweed",
            category = "Quran",
            dayNo = 1,
            maxScore = 100,
            judgeName = "ক্বারী মাহমুদুল হাসান (Judge 2)",
            status = "LIVE"
        ),
        CompetitionEntity(
            id = 3,
            nameBn = "ইসলামিক সাধারণ জ্ঞান ও আইকিউ কুইজ",
            nameEn = "Islamic General Knowledge & IQ Quiz",
            category = "Quiz",
            dayNo = 2,
            maxScore = 100,
            judgeName = "প্রফেসর আমিনুল ইসলাম (Judge 3)",
            status = "SCHEDULED"
        ),
        CompetitionEntity(
            id = 4,
            nameBn = "আরবি ও বাংলা ক্যালিগ্রাফি প্রতিযোগিতা",
            nameEn = "Arabic & Bangla Calligraphy",
            category = "Arts",
            dayNo = 2,
            maxScore = 100,
            judgeName = "শিল্পী নাজমুল হক (Judge 4)",
            status = "SCHEDULED"
        ),
        CompetitionEntity(
            id = 5,
            nameBn = "তাৎক্ষণিক বক্তৃতা ও দলনেতৃত্ব",
            nameEn = "Extempore Speech & Leadership",
            category = "Speech",
            dayNo = 3,
            maxScore = 100,
            judgeName = "ব্রাদার তানভীর হোসাইন (Judge 5)",
            status = "SCHEDULED"
        ),
        CompetitionEntity(
            id = 6,
            nameBn = "শারীরিক সক্ষমতা ও অবস্টাকল রেস",
            nameEn = "Fitness & Obstacle Challenge",
            category = "Sports",
            dayNo = 4,
            maxScore = 100,
            judgeName = "কোচ ফারহান কবির (Judge 6)",
            status = "SCHEDULED"
        )
    )

    val judges = listOf(
        JudgeEntity(
            id = 1,
            nameBn = "মাওলানা আব্দুল গাফফার",
            nameEn = "Mawlana Abdul Ghaffar",
            designation = "মুহাদ্দিস ও প্রধান খতীব, ইসলামিক রিসার্চ সেন্টার",
            assignedCategory = "Hadith",
            phone = "01712-334455",
            notes = "হাদিস মুখস্থকরণ ও তরজমা প্রতিযোগিতার প্রধান বিচারক"
        ),
        JudgeEntity(
            id = 2,
            nameBn = "ক্বারী মাহমুদুল হাসান",
            nameEn = "Qari Mahmudul Hasan",
            designation = "আন্তর্জাতিক পুরস্কারপ্রাপ্ত ক্বারী ও তাজবীদ প্রশিক্ষক",
            assignedCategory = "Quran",
            phone = "01815-667788",
            notes = "হিফজুল কুরআন ও ক্বিরাত প্রতিযোগিতার সমন্বয়ক"
        ),
        JudgeEntity(
            id = 3,
            nameBn = "প্রফেসর ড. আমিনুল ইসলাম",
            nameEn = "Prof. Dr. Aminul Islam",
            designation = "অধ্যাপক, ইসলামিক স্টাডিজ বিভাগ, ঢাবি",
            assignedCategory = "Quiz",
            phone = "01911-223344",
            notes = "ইসলামিক সাধারণ জ্ঞান ও আইকিউ কুইজ জুরিবোর্ড প্রধান"
        ),
        JudgeEntity(
            id = 4,
            nameBn = "শিল্পী নাজমুল হক",
            nameEn = "Artist Nazmul Haque",
            designation = "চারুকলা অনুষদ শিক্ষক ও ক্যালিগ্রাফি বিশেষজ্ঞ",
            assignedCategory = "Arts",
            phone = "01614-778899",
            notes = "আরবি ও বাংলা ক্যালিগ্রাফি ও চিত্রাঙ্কন বিচারক"
        ),
        JudgeEntity(
            id = 5,
            nameBn = "ব্রাদার তানভীর হোসাইন",
            nameEn = "Brother Tanvir Hossain",
            designation = "ন্যাশনাল ডিবেট চ্যাম্পিয়ন ও মোটিভেশনাল স্পিকার",
            assignedCategory = "Speech",
            phone = "01515-889900",
            notes = "তাৎক্ষণিক বক্তৃতা ও বিতর্ক প্রতিযোগিতা মূল্যায়নকারী"
        ),
        JudgeEntity(
            id = 6,
            nameBn = "কোচ ফারহান কবির",
            nameEn = "Coach Farhan Kabir",
            designation = "বিকেএসপি সনদপ্রাপ্ত ফিটনেস ও সারভাইভাল ট্রেইনার",
            assignedCategory = "Sports",
            phone = "01718-990011",
            notes = "শারীরিক সক্ষমতা, অবস্টাকল ও রাফটিং ইভেন্ট বিচারক"
        )
    )

    val inventoryItems = listOf(
        InventoryItemEntity(id = 1, nameBn = "গোল্ড মেডেল (স্বর্ণপদক)", nameEn = "Gold Medals", category = "AWARD", openingQty = 40, issuedQty = 6, balanceQty = 34, lowStockThreshold = 10, unit = "টি"),
        InventoryItemEntity(id = 2, nameBn = "সিলভার মেডেল (রৌপ্যপদক)", nameEn = "Silver Medals", category = "AWARD", openingQty = 50, issuedQty = 10, balanceQty = 40, lowStockThreshold = 15, unit = "টি"),
        InventoryItemEntity(id = 3, nameBn = "ব্রোঞ্জ মেডেল (তাম্রপদক)", nameEn = "Bronze Medals", category = "AWARD", openingQty = 60, issuedQty = 12, balanceQty = 48, lowStockThreshold = 20, unit = "টি"),
        InventoryItemEntity(id = 4, nameBn = "চ্যাম্পিয়ন ক্রেস্ট (গ্রুপ শিরোপা)", nameEn = "Champion Crest", category = "AWARD", openingQty = 12, issuedQty = 0, balanceQty = 12, lowStockThreshold = 3, unit = "টি"),
        InventoryItemEntity(id = 5, nameBn = "রানার-আপ ট্রফি", nameEn = "Runner-up Trophy", category = "AWARD", openingQty = 18, issuedQty = 0, balanceQty = 18, lowStockThreshold = 5, unit = "টি"),
        InventoryItemEntity(id = 6, nameBn = "বিশেষ পারফরম্যান্স ক্রেস্ট", nameEn = "Special Performance Crest", category = "AWARD", openingQty = 25, issuedQty = 4, balanceQty = 21, lowStockThreshold = 5, unit = "টি"),
        InventoryItemEntity(id = 7, nameBn = "স্কুল ব্যাকপ্যাক (প্রিমিয়াম)", nameEn = "School Backpacks", category = "GIFT", openingQty = 40, issuedQty = 8, balanceQty = 32, lowStockThreshold = 10, unit = "টি"),
        InventoryItemEntity(id = 8, nameBn = "চামড়ায় বাঁধানো কুরআন শরীফ", nameEn = "Hardbound Noble Quran", category = "GIFT", openingQty = 50, issuedQty = 15, balanceQty = 35, lowStockThreshold = 15, unit = "টি"),
        InventoryItemEntity(id = 9, nameBn = "রিয়াদুস সালেহীন হাদিস গ্রন্থ", nameEn = "Riyad-us-Saliheen Book", category = "GIFT", openingQty = 35, issuedQty = 10, balanceQty = 25, lowStockThreshold = 10, unit = "টি"),
        InventoryItemEntity(id = 10, nameBn = "সীরাত গ্রন্থ সমগ্র (২ খণ্ড)", nameEn = "Seerah Book Sets", category = "GIFT", openingQty = 40, issuedQty = 5, balanceQty = 35, lowStockThreshold = 10, unit = "সেট"),
        InventoryItemEntity(id = 11, nameBn = "আধুনিক বিজ্ঞান ও উদ্ভাবন বই", nameEn = "Science Book Sets", category = "GIFT", openingQty = 35, issuedQty = 8, balanceQty = 27, lowStockThreshold = 8, unit = "সেট"),
        InventoryItemEntity(id = 12, nameBn = "ডিজিটাল ইসলামিক স্মার্ট ওয়াচ", nameEn = "Digital Islamic Watch", category = "AWARD", openingQty = 30, issuedQty = 6, balanceQty = 24, lowStockThreshold = 8, unit = "টি"),
        InventoryItemEntity(id = 13, nameBn = "স্টেইনলেস থার্মাস ফ্লাস্ক বোতল", nameEn = "Smart Thermos Bottle", category = "GIFT", openingQty = 35, issuedQty = 12, balanceQty = 23, lowStockThreshold = 10, unit = "টি"),
        InventoryItemEntity(id = 14, nameBn = "ড্রয়িং ও ক্যালিগ্রাফি আর্ট বক্স", nameEn = "Calligraphy & Art Set", category = "STATIONERY", openingQty = 25, issuedQty = 5, balanceQty = 20, lowStockThreshold = 5, unit = "সেট"),
        InventoryItemEntity(id = 15, nameBn = "প্রফেশনাল জ্যামিতি বক্স ও কম্পাস", nameEn = "Geometry & Compass Kit", category = "STATIONERY", openingQty = 43, issuedQty = 14, balanceQty = 29, lowStockThreshold = 12, unit = "সেট"),
        InventoryItemEntity(id = 16, nameBn = "প্রিমিয়াম মেটাল পেন ও ডায়েরি", nameEn = "Executive Pen & Diary Set", category = "STATIONERY", openingQty = 40, issuedQty = 10, balanceQty = 30, lowStockThreshold = 10, unit = "সেট"),
        InventoryItemEntity(id = 17, nameBn = "ক্যাম্প জার্সি ও স্পোর্টস ক্যাপ", nameEn = "Camp Jersey & Cap Set", category = "KIT", openingQty = 35, issuedQty = 35, balanceQty = 0, lowStockThreshold = 10, unit = "সেট"),
        InventoryItemEntity(id = 18, nameBn = "ফার্স্ট এইড পার্সোনাল কিট", nameEn = "Personal First Aid Kit", category = "HEALTH", openingQty = 25, issuedQty = 6, balanceQty = 19, lowStockThreshold = 6, unit = "টি"),
        InventoryItemEntity(id = 19, nameBn = "রিচার্জেবল ক্যাম্পিং হেডলাইট", nameEn = "Camping LED Headlight", category = "KIT", openingQty = 20, issuedQty = 4, balanceQty = 16, lowStockThreshold = 5, unit = "টি"),
        InventoryItemEntity(id = 20, nameBn = "সারভাইভাল কম্পাস ও হুইসেল", nameEn = "Survival Compass Kit", category = "KIT", openingQty = 20, issuedQty = 5, balanceQty = 15, lowStockThreshold = 5, unit = "সেট"),
        InventoryItemEntity(id = 21, nameBn = "ব্যাডমিন্টন র‍্যাকেট সেট", nameEn = "Badminton Racket Set", category = "KIT", openingQty = 15, issuedQty = 2, balanceQty = 13, lowStockThreshold = 4, unit = "সেট"),
        InventoryItemEntity(id = 22, nameBn = "ম্যাচ ফুটবল (সাইজ-৫)", nameEn = "Match Football Size 5", category = "KIT", openingQty = 10, issuedQty = 3, balanceQty = 7, lowStockThreshold = 3, unit = "টি"),
        InventoryItemEntity(id = 23, nameBn = "ভলিবল ও নেট সেট", nameEn = "Volleyball & Net Set", category = "KIT", openingQty = 8, issuedQty = 2, balanceQty = 6, lowStockThreshold = 2, unit = "সেট"),
        InventoryItemEntity(id = 24, nameBn = "দাবা বোর্ড ও রুবিকস কিউব", nameEn = "Chess Board & Rubik Set", category = "GIFT", openingQty = 20, issuedQty = 5, balanceQty = 15, lowStockThreshold = 5, unit = "সেট"),
        InventoryItemEntity(id = 25, nameBn = "কুরআন তিলাওয়াত পোর্টেবল স্পিকার", nameEn = "Portable Quran Speaker", category = "AWARD", openingQty = 12, issuedQty = 2, balanceQty = 10, lowStockThreshold = 3, unit = "টি"),
        InventoryItemEntity(id = 26, nameBn = "স্টেইনলেস লাঞ্চ বক্স সেট", nameEn = "Stainless Tiffin Set", category = "GIFT", openingQty = 20, issuedQty = 4, balanceQty = 16, lowStockThreshold = 5, unit = "সেট"),
        InventoryItemEntity(id = 27, nameBn = "ক্যাম্প ফটো ফ্রেম মেমেন্টো", nameEn = "Camp Photo Frame Memento", category = "AWARD", openingQty = 20, issuedQty = 0, balanceQty = 20, lowStockThreshold = 5, unit = "টি"),
        InventoryItemEntity(id = 28, nameBn = "লাইফ স্কিলস মেডেলিয়ন রিবন", nameEn = "Medallion Lanyard Ribbon", category = "AWARD", openingQty = 16, issuedQty = 16, balanceQty = 0, lowStockThreshold = 4, unit = "টি")
    )

    val bulletins = listOf(
        BulletinEntity(
            id = 1,
            dayNo = 1,
            dateStr = "২৭ সেপ্টেম্বর ২০২৬",
            titleBn = "উদ্বোধনী দিনে উচ্ছ্বসিত ৬০৩ তরুণ প্রাণ: শুরু হলো ক্যাম্প বন্ধু ২০২৬",
            titleEn = "603 Youth Gather: Life Skills Camp 2026 Begins with Grand Zeal",
            summaryBn = "সারাদেশের ৬৪ জেলা থেকে আগত শিশু ও এতিম শিক্ষার্থীরা আজ সকালে ক্যাম্প প্রাঙ্গণে একত্রিত হয়েছে। ফজর জামাত ও তালীমুল কুরআনের মধ্য দিয়ে কার্যক্রম শুরু হয়। ৬টি প্রধান গ্রুপ তাদের দলনেতাদের তত্ত্বাবধানে সুশৃঙ্খলভাবে স্টেশন রোটেশনে অংশ নিয়েছে।",
            summaryEn = "Over 603 participants from all across Bangladesh gathered at the central camp premises. Following Fajr prayer, 6 major groups entered their respective stations with utmost discipline.",
            author = "মুহাম্মদ জাকারিয়া (চিফ এডিটর)",
            groupSpotlight = "সালাহউদ্দীন আইয়ুবী গ্রুপ আজ সকালের শরীরচর্চায় প্রথম স্থান অর্জন করেছে।",
            likesCount = 58
        ),
        BulletinEntity(
            id = 2,
            dayNo = 1,
            dateStr = "২৭ সেপ্টেম্বর ২০২৬ - সন্ধ্যা সংস্করণ",
            titleBn = "স্টেশন কার্যক্রমে নতুন উদ্ভাবন: ফার্স্ট এইড ও স্টেম ল্যাবে তরুণদের মেধা",
            titleEn = "Innovation in Stations: STEM & First Aid Labs Shine",
            summaryBn = "স্টেশন ৩ ও ৪-এ অংশগ্রহণকারীরা রোবোটিক্স মডেল ও জরুরি ব্যান্ডেজ বাঁধার কৌশল চমৎকারভাবে সম্পন্ন করেছে। ক্যাম্প চিফ প্রতিটি স্টেশন পরিদর্শন করে সন্তোষ প্রকাশ করেন।",
            summaryEn = "Station 3 and 4 showed remarkable enthusiasm in robotics experiments and practical life-saving medical demonstrations.",
            author = "ক্যাম্প বুলেটিন টিম",
            groupSpotlight = "সুমাইয়া গ্রুপ দেয়াল পত্রিকায় প্রথম খসড়া জমা দিয়েছে।",
            likesCount = 42
        )
    )

    val emergencyContacts = listOf(
        EmergencyContact(
            roleTitleBn = "ক্যাম্প চিফ (প্রধান তত্ত্বাবধায়ক)",
            roleTitleEn = "Camp Chief",
            officerName = "ড. এনামুল হক চৌধুরী",
            phone = "01711-000111",
            location = "এডমিন ব্লক, কক্ষ ১০১"
        ),
        EmergencyContact(
            roleTitleBn = "মেডিকেল ও জরুরি চিকিৎসা টিম",
            roleTitleEn = "Chief Medical Officer",
            officerName = "ডা. তারিকুল ইসলাম (এমবিবিএস, বিসিএস)",
            phone = "01819-222333",
            location = "ক্যাম্প ক্লিনিক ও এম্বুলেন্স বে"
        ),
        EmergencyContact(
            roleTitleBn = "নিরাপত্তা ও সেইফগার্ড ইন-চার্জ",
            roleTitleEn = "Head of Camp Security",
            officerName = "ক্যাপ্টেন (অব.) মোজাম্মেল হক",
            phone = "01912-333444",
            location = "সেন্ট্রাল গেইট কন্ট্রোল রুম"
        ),
        EmergencyContact(
            roleTitleBn = "লজিস্টিকস ও পরিবহন টিম",
            roleTitleEn = "Transport & Logistics Lead",
            officerName = "ইঞ্জিনিয়ার রেজওয়ান করিম",
            phone = "01611-444555",
            location = "যানবাহন ও স্টোর শেড"
        ),
        EmergencyContact(
            roleTitleBn = "ফুড ও ডাইনিং ম্যানেজার",
            roleTitleEn = "Nutrition & Dining Manager",
            officerName = "মুহাম্মদ কামরুল হাসান",
            phone = "01511-555666",
            location = "সেন্ট্রাল ক্যাফেটেরিয়া"
        )
    )

    val rooms = listOf(
        RoomEntity(id = 1, buildingName = "তাজমহল হল", roomNo = "১০১", floor = "১ম তলা", capacity = 12, assignedCount = 12, assignedGroupId = "bukhari", inChargeName = "মুহাম্মদ জুবায়ের", inChargePhone = "01711-101001", notes = "বুখারী সাব-১ ও সাব-২"),
        RoomEntity(id = 2, buildingName = "তাজমহল হল", roomNo = "১০২", floor = "১ম তলা", capacity = 12, assignedCount = 12, assignedGroupId = "bukhari", inChargeName = "আহমেদ হাসিব", inChargePhone = "01711-101002", notes = "বুখারী সাব-৩ ও সাব-৪"),
        RoomEntity(id = 3, buildingName = "তাজমহল হল", roomNo = "১০৩", floor = "১ম তলা", capacity = 12, assignedCount = 12, assignedGroupId = "sumaiya", inChargeName = "ফারহানা ইয়াসমিন", inChargePhone = "01819-101003", notes = "সুমাইয়া সাব-১ ও সাব-২"),
        RoomEntity(id = 4, buildingName = "তাজমহল হল", roomNo = "১০৪", floor = "১ম তলা", capacity = 12, assignedCount = 12, assignedGroupId = "sumaiya", inChargeName = "রুমানা আফরোজ", inChargePhone = "01819-101004", notes = "সুমাইয়া সাব-৩ ও সাব-৪"),
        RoomEntity(id = 5, buildingName = "তাজমহল হল", roomNo = "১০৫", floor = "১ম তলা", capacity = 12, assignedCount = 12, assignedGroupId = "shafeyi", inChargeName = "মাহমুদুর রহমান", inChargePhone = "01912-101005", notes = "শাফেয়ী সাব-১ ও সাব-২"),
        RoomEntity(id = 6, buildingName = "তাজমহল হল", roomNo = "১০৬", floor = "১ম তলা", capacity = 12, assignedCount = 12, assignedGroupId = "shafeyi", inChargeName = "সাঈদ আনোয়ার", inChargePhone = "01912-101006", notes = "শাফেয়ী সাব-৩ ও সাব-৪"),
        RoomEntity(id = 7, buildingName = "শহীদ মিনার ভবন", roomNo = "২০১", floor = "২য় তলা", capacity = 12, assignedCount = 12, assignedGroupId = "umm_ayman", inChargeName = "সাবিহা পারভীন", inChargePhone = "01722-201001", notes = "উম্মে আয়মান সাব-১ ও ২"),
        RoomEntity(id = 8, buildingName = "শহীদ মিনার ভবন", roomNo = "২০২", floor = "২য় তলা", capacity = 12, assignedCount = 12, assignedGroupId = "umm_ayman", inChargeName = "নুসরাত জাহান", inChargePhone = "01722-201002", notes = "উম্মে আয়মান সাব-৩ ও ৪"),
        RoomEntity(id = 9, buildingName = "শহীদ মিনার ভবন", roomNo = "২০৩", floor = "২য় তলা", capacity = 12, assignedCount = 12, assignedGroupId = "salahuddin", inChargeName = "রায়হান উদ্দিন", inChargePhone = "01611-201003", notes = "সালাহউদ্দীন সাব-১ ও ২"),
        RoomEntity(id = 10, buildingName = "শহীদ মিনার ভবন", roomNo = "২০৪", floor = "২য় তলা", capacity = 12, assignedCount = 12, assignedGroupId = "salahuddin", inChargeName = "তারেক মাহমুদ", inChargePhone = "01611-201004", notes = "সালাহউদ্দীন সাব-৩ ও ৪"),
        RoomEntity(id = 11, buildingName = "শহীদ মিনার ভবন", roomNo = "২০৫", floor = "২য় তলা", capacity = 12, assignedCount = 12, assignedGroupId = "umm_saad", inChargeName = "নাসরিন জাহান", inChargePhone = "01511-201005", notes = "উম্মে সা'দ সাব-১ ও ২"),
        RoomEntity(id = 12, buildingName = "শহীদ মিনার ভবন", roomNo = "২০৬", floor = "২য় তলা", capacity = 12, assignedCount = 12, assignedGroupId = "umm_saad", inChargeName = "সালমা বেগম", inChargePhone = "01511-201006", notes = "উম্মে সা'দ সাব-৩ ও ৪")
    )

    val travelVehicles = listOf(
        TravelVehicleEntity(id = 1, vehicleNo = "বাস ১ (ঢাকা মেট্রো ব-১১-২৩৪৫)", driverName = "মো. রফিক মিয়া", driverPhone = "01711-888001", inChargeName = "হাফেজ তানভীর আহমেদ", inChargePhone = "01711-234567", capacity = 40, assignedGroupId = "bukhari", departureHeadcount = 38, returnHeadcount = 38, status = "SCHEDULED"),
        TravelVehicleEntity(id = 2, vehicleNo = "বাস ২ (ঢাকা মেট্রো ব-১১-২৩৪৬)", driverName = "মো. বাবুল হোসেন", driverPhone = "01819-888002", inChargeName = "মুহতারামা খাদিজা আক্তার", inChargePhone = "01819-345678", capacity = 40, assignedGroupId = "sumaiya", departureHeadcount = 39, returnHeadcount = 39, status = "SCHEDULED"),
        TravelVehicleEntity(id = 3, vehicleNo = "বাস ৩ (ঢাকা মেট্রো ব-১১-২৩৪৭)", driverName = "মো. সেলিম উদ্দিন", driverPhone = "01912-888003", inChargeName = "মাওলানা আব্দুল গাফফার", inChargePhone = "01912-456789", capacity = 40, assignedGroupId = "shafeyi", departureHeadcount = 38, returnHeadcount = 38, status = "SCHEDULED"),
        TravelVehicleEntity(id = 4, vehicleNo = "বাস ৪ (ঢাকা মেট্রো ব-১১-২৩৪৮)", driverName = "মো. কালাম মিয়া", driverPhone = "01611-888004", inChargeName = "মুহতারামা সাবিহা পারভীন", inChargePhone = "01722-567890", capacity = 40, assignedGroupId = "umm_ayman", departureHeadcount = 39, returnHeadcount = 39, status = "SCHEDULED"),
        TravelVehicleEntity(id = 5, vehicleNo = "বাস ৫ (ঢাকা মেট্রো ব-১১-২৩৪৯)", driverName = "মো. খোরশেদ আলম", driverPhone = "01511-888005", inChargeName = "ইঞ্জিনিয়ার রায়হান উদ্দিন", inChargePhone = "01611-678901", capacity = 40, assignedGroupId = "salahuddin", departureHeadcount = 38, returnHeadcount = 38, status = "SCHEDULED"),
        TravelVehicleEntity(id = 6, vehicleNo = "বাস ৬ (ঢাকা মেট্রো ব-১১-২৩৫০)", driverName = "মো. ফারুক হাসান", driverPhone = "01712-888006", inChargeName = "মুহতারামা নাসরিন জাহান", inChargePhone = "01511-789012", capacity = 40, assignedGroupId = "umm_saad", departureHeadcount = 39, returnHeadcount = 39, status = "SCHEDULED")
    )

    val dutyRosters = listOf(
        DutyRosterEntity(id = 1, committeeKey = "MANAGEMENT", committeeNameBn = "ব্যবস্থাপনা ও সমন্বয়", taskTitle = "ক্যাম্প উদ্বোধনী ও সমাপনী সার্বিক তদারকি", assignedPersonName = "ড. এনামুল হক চৌধুরী", assignedPersonPhone = "01711-000111", shiftTime = "সার্বক্ষণিক", location = "সেন্ট্রাল কন্ট্রোল", status = "IN_PROGRESS"),
        DutyRosterEntity(id = 2, committeeKey = "DISCIPLINE", committeeNameBn = "শৃঙ্খলা ও নিরাপত্তা", taskTitle = "গেট পাস, নাইট পেট্রোলিং ও লাইট অফ তদারকি", assignedPersonName = "ক্যাপ্টেন মোজাম্মেল হক", assignedPersonPhone = "01912-333444", shiftTime = "রাত ১০:০০ - সকাল ০৬:০০", location = "প্রধান ফটক ও মাঠ", status = "IN_PROGRESS"),
        DutyRosterEntity(id = 3, committeeKey = "DINING", committeeNameBn = "খাদ্য ও ক্যাটারিং", taskTitle = "৩ বেলা স্বাস্থ্যসম্মত খাদ্য মান যাচাই ও সময়মতো পরিবেশন", assignedPersonName = "মুহাম্মদ কামরুল হাসান", assignedPersonPhone = "01511-555666", shiftTime = "সকাল ০৬:০০ - রাত ১০:০০", location = "সেন্ট্রাল ডাইনিং", status = "IN_PROGRESS"),
        DutyRosterEntity(id = 4, committeeKey = "ACCOUNTS", committeeNameBn = "হিসাব ও অডিট", taskTitle = "দৈনিক ভাউচার পরীক্ষা, কিট অডিট ও স্টক ছাড়করণ", assignedPersonName = "মুহাম্মদ তারিক রহমান", assignedPersonPhone = "01811-777888", shiftTime = "সকাল ০৯:০০ - বিকাল ০৫:০০", location = "অ্যাকাউন্টিং ডেস্ক", status = "COMPLETED"),
        DutyRosterEntity(id = 5, committeeKey = "EVALUATION", committeeNameBn = "মূল্যায়ন ও মনিটরিং", taskTitle = "স্টেশন কার্যকারিতা মূল্যায়ন ও প্রশিক্ষণার্থী ফিডব্যাক সংগ্রহ", assignedPersonName = "প্রফেসর আমিনুল ইসলাম", assignedPersonPhone = "01715-999000", shiftTime = "প্রতি স্টেশন শেষে", location = "মনিটরিং সেল", status = "IN_PROGRESS"),
        DutyRosterEntity(id = 6, committeeKey = "MEDIA", committeeNameBn = "মিডিয়া ও প্রকাশনা", taskTitle = "দৈনিক বুলেটিন সম্পাদনা, দেয়ালিকা চূড়ান্ত ও ফটোগ্রাফি", assignedPersonName = "মাহবুব মোরশেদ", assignedPersonPhone = "01618-111222", shiftTime = "দুপুর ১২:০০ ও সন্ধ্যা ০৭:০০", location = "প্রেস ও পাবলিকেশন", status = "IN_PROGRESS"),
        DutyRosterEntity(id = 7, committeeKey = "HEALTH", committeeNameBn = "স্বাস্থ্য ও সেবা", taskTitle = "ফার্স্ট এইড, ডেন্টাল/আই চেকআপ ও জরুরি ক্লিনিক সেবা", assignedPersonName = "ডা. তারিকুল ইসলাম", assignedPersonPhone = "01819-222333", shiftTime = "২৪ ঘণ্টা জরুরি", location = "ক্যাম্প ক্লিনিক", status = "IN_PROGRESS"),
        DutyRosterEntity(id = 8, committeeKey = "TRANSPORT", committeeNameBn = "পরিবহন ও সফর", taskTitle = "শিক্ষা সফর বাস ব্যবস্থাপনা ও সিকিউরিটি হেডকাউন্ট", assignedPersonName = "ইঞ্জিনিয়ার রেজওয়ান করিম", assignedPersonPhone = "01611-444555", shiftTime = "দিন ৪ সারাদিন", location = "বাস পার্কিং জোন", status = "PENDING")
    )

    val campGoals = listOf(
        CampGoalMetricEntity(id = 1, metricTitleBn = "সামগ্রিক জামায়াত ও অধিবেশন হাজিরা হার", targetValue = 98.0, currentValue = 96.4, unit = "%", category = "হাজিরা"),
        CampGoalMetricEntity(id = 2, metricTitleBn = "সময়নিষ্ঠতা ও যথাসময়ে সেশন শুরুর হার", targetValue = 100.0, currentValue = 94.0, unit = "%", category = "সময়নিষ্ঠতা"),
        CampGoalMetricEntity(id = 3, metricTitleBn = "মেডিকেল ও ডেন্টাল হেলথ কভারেজ", targetValue = 100.0, currentValue = 88.5, unit = "%", category = "স্বাস্থ্য"),
        CampGoalMetricEntity(id = 4, metricTitleBn = "প্রশিক্ষণার্থী সন্তুষ্টি ও ইতিবাচক ফিডব্যাক", targetValue = 95.0, currentValue = 92.0, unit = "%", category = "ফিডব্যাক"),
        CampGoalMetricEntity(id = 5, metricTitleBn = "শিক্ষা সফর রিটার্ন হেডকাউন্ট কমপ্লায়েন্স", targetValue = 100.0, currentValue = 100.0, unit = "%", category = "নিরাপত্তা"),
        CampGoalMetricEntity(id = 6, metricTitleBn = "কিট ও সামগ্রী বিতরণ প্রাপ্তি স্বীকার", targetValue = 100.0, currentValue = 99.2, unit = "%", category = "লজিস্টিকস")
    )

    val staffAccounts = listOf(
        UserAccountEntity(email = "admin@lsc.org", fullName = "সুপার অ্যাডমিন (IT ও ডাটা কন্ট্রোল)", role = "SUPER_ADMIN", passwordHash = "Lsc@Admin2026", phone = "01711-000000"),
        UserAccountEntity(email = "chief@lsc.org", fullName = "ড. এনামুল হক চৌধুরী (ক্যাম্প চিফ)", role = "CAMP_CHIEF", passwordHash = "Lsc@Chief2026", phone = "01711-000111"),
        UserAccountEntity(email = "reg@lsc.org", fullName = "রেজিস্ট্রেশন ও রোস্টার অফিসার", role = "REGISTRATION", passwordHash = "Lsc@Reg2026", phone = "01811-123456"),
        UserAccountEntity(email = "medical@lsc.org", fullName = "ডা. তারিকুল ইসলাম (মেডিকেল ইন-চার্জ)", role = "MEDICAL", passwordHash = "Lsc@Med2026", phone = "01819-222333"),
        UserAccountEntity(email = "judge@lsc.org", fullName = "মাওলানা আব্দুল গাফফার (প্রধান বিচারক)", role = "JUDGE", passwordHash = "Lsc@Judge2026", phone = "01912-456789"),
        UserAccountEntity(email = "logistics@lsc.org", fullName = "ইঞ্জিনিয়ার রেজওয়ান করিম (লজিস্টিকস লিড)", role = "LOGISTICS", passwordHash = "Lsc@Log2026", phone = "01611-444555")
    )

    fun generateKitDistributions(participants: List<ParticipantEntity>): List<KitDistributionEntity> {
        val sizes = listOf("M", "L", "S", "XL", "M")
        return participants.mapIndexed { idx, p ->
            KitDistributionEntity(
                id = (idx + 1).toLong(),
                participantId = p.id,
                campCode = p.campCode,
                participantName = p.nameBn,
                groupName = p.groupNameBn,
                tShirtSize = sizes[idx % sizes.size],
                kitBagStatus = if (idx < 550) "CONFIRMED" else "DISTRIBUTED",
                giftPackStatus = if (idx < 500) "CONFIRMED" else "ALLOCATED",
                idBadgeStatus = "CONFIRMED",
                notebookStatus = "CONFIRMED",
                receivedAt = System.currentTimeMillis() - (idx * 60000L),
                deliveredBy = "লজিস্টিকস ডেস্ক"
            )
        }
    }
}
