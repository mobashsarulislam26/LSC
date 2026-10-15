package com.example.data.model

/**
 * Data structures for the 12 Operational Departments, Standard Operating Procedures (SOPs),
 * 24 Subgroups Directory, and Camp Rationale & Expected Outcomes (Pages 27-34 of LSC 2025 Rationale).
 */

data class DepartmentSop(
    val id: Int,
    val key: String,
    val nameBn: String,
    val nameEn: String,
    val inChargeName: String,
    val inChargePhone: String,
    val assistantName: String = "",
    val location: String,
    val shiftTime: String,
    val coreObjectiveBn: String,
    val responsibilitiesBn: List<String>,
    val sopStepsBn: List<String>,
    val checklistBn: List<String>,
    val iconKey: String = "management"
)

data class SubgroupInfo(
    val id: String, // e.g. "bukhari_sub1"
    val groupId: String, // "bukhari", "sumaiya", etc.
    val groupNameBn: String,
    val groupNameEn: String,
    val subgroupNo: Int, // 1 to 4
    val subgroupCode: String, // "বুখারী সাব-১" / "Bukhari-1"
    val mentorName: String,
    val mentorPhone: String,
    val roomNo: String,
    val buildingName: String,
    val memberCount: Int = 25,
    val colorHex: Long,
    val dutiesBn: String = "দৈনিক হাজিরা, রুম শৃঙ্খলা, জামায়াত উপস্থিতি ও সেশন রোটেশন তদারকি"
)

data class CampRationalePillar(
    val id: Int,
    val titleBn: String,
    val titleEn: String,
    val subtitleBn: String,
    val descriptionBn: String,
    val targetsBn: List<String>,
    val iconKey: String
)

data class ExpectedOutcomeMetric(
    val id: Int,
    val titleBn: String,
    val titleEn: String,
    val targetPercentage: Int,
    val currentPercentage: Int,
    val descriptionBn: String,
    val category: String
)

object CampRationaleData {

    val themeBn = "আত্মগঠন ও সমাজ বিনির্মাণ"
    val themeEn = "Build Thyself, Help to Build Society"
    val eventNameBn = "আবাসিক জীবন দক্ষতা ক্যাম্প ২০২৬ (LSC 2026)"
    val eventNameEn = "Life Skills Camping 2026 (Residential LSC)"

    val rationaleDescriptionBn = """
        'আবাসিক জীবন দক্ষতা ক্যাম্প ২০২৬' তরুণ ও কিশোর শিক্ষার্থীদের বাস্তব জীবনের চ্যালেঞ্জ মোকাবিলায় প্রস্তুত করার একটি সমন্বিত আবাসিক উদ্যোগ। 
        এই ক্যাম্পের মূল দর্শন হলো 'আত্মগঠন ও সমাজ বিনির্মাণ'। প্রতিটি শিক্ষার্থী যেন ব্যক্তিগত চরিত্রে সততা, শৃঙ্খলা ও দ্বীনি মূল্যবোধ ধারণ করে 
        এবং বাস্তবমুখী জীবন দক্ষতা (সারভাইভাল, ফার্স্ট এইড, সমস্যা সমাধান, সৃজনশীলতা ও দলগত নেতৃত্ব) অর্জনের মাধ্যমে দেশ ও সমাজের কার্যকর সম্পদে পরিণত হতে পারে।
    """.trimIndent()

    val pillars = listOf(
        CampRationalePillar(
            id = 1,
            titleBn = "১. আত্মগঠন ও চারিত্রিক উৎকর্ষ",
            titleEn = "Self Development & Character Building",
            subtitleBn = "আখলাক, সুন্নাহর অনুশীলন ও একাগ্র সালাত",
            descriptionBn = "সহীহ পদ্ধতিতে অজু, তাহাজ্জুদ ও জামায়াতে সালাত আদায়, তালীমুল কুরআন, সুন্নাহসম্মত আদব ও চারিত্রিক নির্মলতা অর্জন।",
            targetsBn = listOf(
                "পাঁচ ওয়াক্ত সালাত জামায়াতে আদায়ের অভ্যাস গঠন",
                "মাসনূন দোয়া ও দৈনন্দিন শিষ্টাচারের ব্যবহারিক প্রয়োগ",
                "আত্মপর্যালোচনা (মুহাসাবাহ) ও ইতিবাচক মানসিকতা গঠন"
            ),
            iconKey = "quran"
        ),
        CampRationalePillar(
            id = 2,
            titleBn = "২. ব্যবহারিক জীবন দক্ষতা ও দুর্যোগ ব্যবস্থাপনা",
            titleEn = "Practical Life Skills & Crisis Readiness",
            subtitleBn = "সারভাইভাল, প্রাথমিক চিকিৎসা ও স্বাবলম্বিতা",
            descriptionBn = "জরুরি অবস্থায় জীবন রক্ষা, প্রাথমিক চিকিৎসা (ব্যান্ডেজ, সিপিআর, হিটস্ট্রোক ও ফ্র্যাকচার ব্যবস্থাপনা), অগ্নিনির্বাপণ ও প্রতিকূল পরিবেশ মোকাবিলা।",
            targetsBn = listOf(
                "প্রাথমিক চিকিৎসা কিটের সঠিক ব্যবহার ও ব্যান্ডেজ বাঁধার দক্ষতা",
                "তাঁবু স্থাপন, নট বাঁধার কৌশল ও জরুরি উদ্ধার কার্যপদ্ধতি",
                "ব্যক্তিগত পরিচ্ছন্নতা, সময় ব্যবস্থাপনা ও স্বনির্ভরতা"
            ),
            iconKey = "survival"
        ),
        CampRationalePillar(
            id = 3,
            titleBn = "৩. মানসিক বিকাশ ও সমস্যা সমাধান",
            titleEn = "Mental Resilience & Problem Solving",
            subtitleBn = "সৃজনশীল চিন্তা, রোবোটিক্স ও সিদ্ধান্ত গ্রহণ",
            descriptionBn = "যেকোনো সংকটময় পরিস্থিতিতে আতঙ্কিত না হয়ে যৌক্তিক সিদ্ধান্ত গ্রহণ, সৃজনশীল মননশীলতা ও সমস্যা সমাধানের ক্ষমতা বাড়ানো।",
            targetsBn = listOf(
                "বাস্তবমুখী কেইস স্টাডি ও উদ্ভাবনী সমস্যা সমাধান অনুশীলন",
                "বিজ্ঞান, প্রযুক্তি ও সৃষ্টিশীল মডেল প্রদর্শনী",
                "ইতিবাচক যোগাযোগ ও মানসিক চাপ নিয়ন্ত্রণ কৌশল"
            ),
            iconKey = "science"
        ),
        CampRationalePillar(
            id = 4,
            titleBn = "৪. দলগত শৃঙ্খলা ও পারস্পরিক ভ্রাতৃত্ব",
            titleEn = "Teamwork, Discipline & Brotherhood",
            subtitleBn = "সহমর্মিতা, পারস্পরিক শ্রদ্ধাবোধ ও দলনেতৃত্ব",
            descriptionBn = "৬৪ জেলা থেকে আগত শিক্ষার্থীদের মাঝে হিংসা-বিদ্বেষহীন পারস্পরিক ভালোবাসা, সহমর্মিতা ও দলগত ঐক্যের চমৎকার নিদর্শন স্থাপন।",
            targetsBn = listOf(
                "গ্রুপ ও সাব-গ্রুপ ভিত্তিক শৃঙ্খলা ও যৌথ দায়িত্ব পালন",
                "দুর্বল ও অসুস্থ সহপাঠীকে সহায়তা করার আন্তরিক মনোভাব",
                "নেতৃত্বের নির্দেশ মান্য করা ও গণতান্ত্রিক মতামত প্রদান"
            ),
            iconKey = "teamwork"
        ),
        CampRationalePillar(
            id = 5,
            titleBn = "৫. সমাজ বিনির্মাণ ও স্বেচ্ছাসেবী নেতৃত্ব",
            titleEn = "Social Responsibility & Civic Action",
            subtitleBn = "দেশপ্রেম, জনকল্যাণ ও সামাজিক ক্ষমতায়ন",
            descriptionBn = "ক্যাম্পের প্রশিক্ষণকে কেবল নিজের মাঝে সীমাবদ্ধ না রেখে নিজের এলাকা ও বিদ্যালয়ে সমাজসেবামূলক কাজে আত্মনিয়োগের মানসিকতা।",
            targetsBn = listOf(
                "পরিবেশ সচেতনতা, বৃক্ষরোপণ ও ক্যাম্পাস পরিচ্ছন্নতা চর্চা",
                "সুবিধাবঞ্চিতদের সেবায় এগিয়ে আসার বাস্তব শপথ",
                "ক্যাম্প পরবর্তী সোশ্যাল প্রজেক্ট পরিকল্পনা ও অঙ্গীকার"
            ),
            iconKey = "society"
        )
    )

    val expectedOutcomes = listOf(
        ExpectedOutcomeMetric(
            id = 1,
            titleBn = "জামায়াত ও সালাত প্রশিক্ষণ শতভাগ সম্পন্ন",
            titleEn = "Jamaat & Salah Mastery Completion",
            targetPercentage = 100,
            currentPercentage = 98,
            descriptionBn = "৬০৩ জন প্রশিক্ষণার্থীর সহীহ তিলাওয়াত ও জামায়াতে পাঁচ ওয়াক্ত সালাত প্রতিষ্ঠা",
            category = "আত্মগঠন"
        ),
        ExpectedOutcomeMetric(
            id = 2,
            titleBn = "জীবন দক্ষতা স্টেশনে সক্রিয় অংশগ্রহণ",
            titleEn = "Life Skills Stations Active Completion",
            targetPercentage = 100,
            currentPercentage = 96,
            descriptionBn = "৬টি বিশেষায়িত স্টেশনের প্রতিটি ব্যবহারিক অনুশীলনে সফল অংশগ্রহণ",
            category = "দক্ষতা"
        ),
        ExpectedOutcomeMetric(
            id = 3,
            titleBn = "মেডিকেল ও ডেন্টাল হেলথ কভারেজ",
            titleEn = "Medical, Dental & Eye Screening",
            targetPercentage = 100,
            currentPercentage = 94,
            descriptionBn = "সকল প্রশিক্ষণার্থীর সাধারণ স্বাস্থ্য, দৃষ্টিশক্তি ও ডেন্টাল চেকআপ কার্ড প্রস্তুত",
            category = "স্বাস্থ্য"
        ),
        ExpectedOutcomeMetric(
            id = 4,
            titleBn = "দলগত সম্প্রীতি ও শৃঙ্খলা সূচক",
            titleEn = "Teamwork & Camp Discipline Index",
            targetPercentage = 95,
            currentPercentage = 92,
            descriptionBn = "২৪টি সাব-গ্রুপের আন্তঃপারস্পরিক সহযোগিতা ও নিয়ম মানার সম্মিলিত হার",
            category = "শৃঙ্খলা"
        ),
        ExpectedOutcomeMetric(
            id = 5,
            titleBn = "শিক্ষা সফর ও পরিবহন নিরাপত্তা নিশ্চয়তা",
            titleEn = "Study Tour Safety & Headcount Compliance",
            targetPercentage = 100,
            currentPercentage = 100,
            descriptionBn = "ঐতিহাসিক সোনারগাঁ শিক্ষা সফরে শতভাগ নিরাপদ যাতায়াত ও রিটার্ন হেডকাউন্ট",
            category = "নিরাপত্তা"
        ),
        ExpectedOutcomeMetric(
            id = 6,
            titleBn = "সমাজসেবা ও প্রজেক্ট অঙ্গীকার পত্র গ্রহণ",
            titleEn = "Social Impact Action Pledge",
            targetPercentage = 100,
            currentPercentage = 95,
            descriptionBn = "ক্যাম্প সমাপ্তির পর নিজ এলাকায় সমাজসেবামূলক কাজের লিখিত প্রতিশ্রুতি জমা",
            category = "সমাজ বিনির্মাণ"
        )
    )

    // Complete 12 Departments from Pages 28 to 33 of LSC 2025 Rationale
    val departments = listOf(
        DepartmentSop(
            id = 1,
            key = "SECRETARIAT",
            nameBn = "১. ক্যাম্প পরিচালনা ও কেন্দ্রীয় সমন্বয় বিভাগ",
            nameEn = "1. Camp Management & Central Secretariat",
            inChargeName = "ড. এনামুল হক চৌধুরী (Camp Chief)",
            inChargePhone = "01711-000111",
            assistantName = "মুহাম্মদ জাকারিয়া (কো-অর্ডিনেটর)",
            location = "সেন্ট্রাল কন্ট্রোল রুম, এডমিন ব্লক ১০১",
            shiftTime = "সার্বক্ষণিক (২৪ ঘণ্টা)",
            coreObjectiveBn = "ক্যাম্পের সকল কার্যক্রমের কেন্দ্রীয় নিয়ন্ত্রণ, সার্বিক শৃঙ্খলা রক্ষা এবং উচ্চ পর্যায়ের নীতিনির্ধারণী সমন্বয়।",
            responsibilitiesBn = listOf(
                "ক্যাম্পের সার্বিক শৃঙ্খলা, নিরাপত্তা ও মানসম্মত পরিচালনা তদারকি করা।",
                "প্রতিদিন ফজরের পর ও এশার পর ১২টি বিভাগের ইন-চার্জদের নিয়ে কো-অর্ডিনেশন মিটিং সম্পন্ন করা।",
                "জরুরি সংকট বা অপ্রত্যাশিত পরিস্থিতি তাৎক্ষণিক নিরসনে সিদ্ধান্ত প্রদান।",
                "প্রতিদিনের কার্যক্রমের অগ্রগতি মূল্যায়ন ও ক্যাম্প বুলেটিন অনুমোদন।"
            ),
            sopStepsBn = listOf(
                "ধাপ ১: প্রতিদিন সকাল ০৫:১৫ টায় কন্ট্রোল রুমে দৈনিক কার্যক্রমের ব্রিফিং প্রস্তুত করা।",
                "ধাপ ২: প্রতিটি সেশনের শুরুর ১৫ মিনিট পূর্বে সংশ্লিষ্ট বিভাগ প্রস্তুত কিনা তা নিশ্চিত করা।",
                "ধাপ ৩: কোনো স্টেশনে বা ডাইনিংয়ে ব্যত্যয় ঘটলে সংশ্লিষ্ট কর্মকর্তাকে তলব করে ১০ মিনিটের মধ্যে সমাধান করা।",
                "ধাপ ৪: রাত ১১:০০ টায় সমগ্র ক্যাম্পের লাইট অফ ও নিরাপত্তা রিপোর্ট পর্যালোচনা করে দিন শেষ করা।"
            ),
            checklistBn = listOf(
                "সকল বিভাগের দায়িত্ব বণ্টন শিট আপডেট আছে কি?",
                "জরুরি যোগাযোগের ওয়াকি-টকি ও মোবাইল নেটওয়ার্ক সক্রিয় আছে কি?",
                "দৈনিক সমন্বয় সভার সিদ্ধান্ত লিপিবদ্ধ হয়েছে কি?",
                "চিফ স্পিকার ও মেহমানদের সময়সূচি চূড়ান্ত আছে কি?"
            ),
            iconKey = "management"
        ),
        DepartmentSop(
            id = 2,
            key = "DISCIPLINE",
            nameBn = "২. শৃঙ্খলা, নিরাপত্তা ও নাইট পেট্রোলিং বিভাগ",
            nameEn = "2. Discipline, Security & Night Vigilance",
            inChargeName = "ক্যাপ্টেন (অব.) মোজাম্মেল হক",
            inChargePhone = "01912-333444",
            assistantName = "মো. শাহিনুর রহমান",
            location = "প্রধান ফটক ও সেন্ট্রাল সিকিউরিটি পোস্ট",
            shiftTime = "সার্বক্ষণিক (রোস্টারে ৩ শিফট)",
            coreObjectiveBn = "ক্যাম্প প্রাঙ্গণের চতুর্সীমা সুরক্ষিত রাখা, অননুমোদিত প্রবেশ রোধ এবং রাতের নিরাপত্তা নিশ্চিত করা।",
            responsibilitiesBn = listOf(
                "ক্যাম্পের সকল প্রবেশ দ্বারে সার্বক্ষণিক প্রহরা ও গেট পাস পরীক্ষণ।",
                "অননুমোদিত বহিরাগত, হকার বা অনুমতিহীন ব্যক্তির ক্যাম্পাসে প্রবেশ সম্পূর্ণ নিষিদ্ধ করা।",
                "রাত ১০:৩০ টায় ডরমিটরিতে লাইট-অফ নিশ্চিত করা এবং মাঠ চত্বর খালি করা।",
                "রাত ১১:০০ টা থেকে ভোর ০৪:৩০ টা পর্যন্ত ঘণ্টাব্যাপী ক্যাম্প চত্বর ও হোস্টেল পেরিমিটারে টহল প্রদান।"
            ),
            sopStepsBn = listOf(
                "ধাপ ১: গেটে দায়িত্বপ্রাপ্ত ভলান্টিয়ারদের ডিজিটাল রেজিস্টার ও ভিজিটর ব্যাজ সরবরাহ করা।",
                "ধাপ ২: কোনো শিক্ষার্থী বা স্টাফ গেট ছাড়তে চাইলে দায়িত্বপ্রাপ্ত গ্রুপ লিডারের লিখিত অনুমতি যাচাই করা।",
                "ধাপ ৩: সেশন চলাকালে কোনো শিক্ষার্থী ডরমিটরি বা অলস ঘোরাঘুরি করছে কিনা তা চেক করে ক্লাসে পাঠানো।",
                "ধাপ ৪: মধ্যরাতে সন্দেহজনক শব্দ বা আলো চোখে পড়লে তাৎক্ষণিক ২ জন গার্ড গিয়ে কারণ অনুসন্ধান করা।"
            ),
            checklistBn = listOf(
                "প্রধান ফটকে রেজিস্টার ও মেটাল ডিটেক্টর সক্রিয় আছে কি?",
                "নাইট পেট্রোলিং টিমের টর্চলাইট ও হুইসেল কার্যকর আছে কি?",
                "হোস্টেল রুমের পেছনের সীমানা প্রাচীরে পর্যাপ্ত আলোকসজ্জা আছে কি?",
                "জরুরি ফায়ার এক্সটিংগুইশার প্রস্তুত আছে কি?"
            ),
            iconKey = "security"
        ),
        DepartmentSop(
            id = 3,
            key = "CATERING",
            nameBn = "৩. খাদ্য, পুষ্টি ও ক্যাটারিং বিভাগ",
            nameEn = "3. Food, Nutrition & Dining Management",
            inChargeName = "মুহাম্মদ কামরুল হাসান",
            inChargePhone = "01511-555666",
            assistantName = "মো. রফিকুল ইসলাম",
            location = "সেন্ট্রাল ক্যাফেটেরিয়া ও ডাইনিং হল",
            shiftTime = "সকাল ০৫:০০ - রাত ১১:০০",
            coreObjectiveBn = "৬০০+ প্রশিক্ষণার্থী ও স্টাফদের সুস্বাদু, স্বাস্থ্যসম্মত ও পুষ্টিকর খাবার যথাসময়ে পরিবেশন।",
            responsibilitiesBn = listOf(
                "৩ বেলা মূল খাবার (নাস্তা, মধ্যাহ্নভোজ, নৈশভোজ) এবং ২ বেলা হালকা নাস্তা মানসম্মতভাবে পরিবেশন।",
                "খাদ্যের কাঁচামাল ও রান্নার তেলের বিশুদ্ধতা এবং বাবুর্চিদের হাইজিন কঠোরভাবে নিয়ন্ত্রণ করা।",
                "ঝাল কম বা বিশেষ পথ্য প্রয়োজন এমন শিক্ষার্থীদের জন্য আলাদা খাবার কাউন্টার নিশ্চিত করা।",
                "খাবার গ্রহণের সময় কিউআর স্ক্যানারের মাধ্যমে নিশ্চিত করা যেন কেউ অভুক্ত না থাকে ও অপচয় না হয়।"
            ),
            sopStepsBn = listOf(
                "ধাপ ১: খাবার পরিবেশনের ঠিক ১ ঘণ্টা পূর্বে ক্যাটারিং ইন-চার্জ খাবারের নমুনা পরীক্ষা (টেস্টিং) করবেন।",
                "ধাপ ২: সেশনের সমাপ্তির ৫ মিনিট পূর্বে খাবার টেবিলে পানি ও ডিশ সাজিয়ে রাখা।",
                "ধাপ ৩: দল ভিত্তিক লাইনে সুশৃঙ্খলভাবে প্রবেশ করানো এবং কিউআর কোড স্ক্যানে খাবার ট্র্যাকিং রাখা।",
                "ধাপ ৪: খাবার শেষে ১৫ মিনিটের মধ্যে ডাইনিং টেবিল জীবাণুমুক্ত করা এবং উদ্বৃত্ত খাবার সংরক্ষণ করা।"
            ),
            checklistBn = listOf(
                "ফিল্টার করা নিরাপদ খাবার পানির ব্যবস্থা পর্যাপ্ত আছে কি?",
                "বাবুর্চি ও পরিবেশকদের অ্যাপ্রন, গ্লাভস ও হেয়ারনেট পরা নিশ্চিত কি?",
                "অসুস্থ প্রশিক্ষণার্থীদের জন্য নরম খিচুড়ি/স্যুপ প্রস্তুত আছে কি?",
                "ডাইনিং কাউন্টারে কিউআর স্ক্যানিং ডিভাইস চার্জ করা আছে কি?"
            ),
            iconKey = "dining"
        ),
        DepartmentSop(
            id = 4,
            key = "ACCOMMODATION",
            nameBn = "৪. আবাসন, কক্ষ বণ্টন ও হোস্টেল ব্যবস্থাপনা বিভাগ",
            nameEn = "4. Accommodation, Room Allotment & Hygiene",
            inChargeName = "প্রকৌশলী তারেক মাহমুদ",
            inChargePhone = "01611-201004",
            assistantName = "মুহাম্মদ হাসিব",
            location = "হোস্টেল ব্লক এ ও বি (তাজমহল ও শহীদ মিনার)",
            shiftTime = "সার্বক্ষণিক",
            coreObjectiveBn = "সকল অংশগ্রহণকারীর আরামদায়ক রাতযাপন, কক্ষভিত্তিক শৃঙ্খলা ও সর্বোচ্চ স্বাস্থ্যবিধি বজায় রাখা।",
            responsibilitiesBn = listOf(
                "১২টি হোস্টেল রুম ও ডরমিটরিতে ২৪টি সাব-গ্রুপের সুনির্দিষ্ট বেড নম্বর বরাদ্দ ও রক্ষণাবেক্ষণ।",
                "প্রতিটি বেডে মশারি, পরিষ্কার চাদর ও বালিশ সরবরাহ নিশ্চিত করা।",
                "হোস্টেলের বাথরুম ও ওয়াশরুম সার্বক্ষণিক পরিষ্কার ও শুকনা রাখা।",
                "রুম ইন-চার্জদের মাধ্যমে কক্ষে লাইট-অফ ও মোবাইল ফোন জমা রাখার নিয়ম তদারকি করা।"
            ),
            sopStepsBn = listOf(
                "ধাপ ১: সকাল ০৭:৩০ এর মধ্যে প্রতিটি রুম পরিদর্শন করে বিছানা গুছানো আছে কিনা চেক করা।",
                "ধাপ ২: সকালের সেশন চলাকালে সুইপার দ্বারা ওয়াশরুম ও ফ্লোর ফিনাইল দিয়ে জীবাণুমুক্ত করা।",
                "ধাপ ৩: দুপুর ০১:৩০ থেকে ০৩:০০ পর্যন্ত পাওয়ার ন্যাপের সময় নিস্তব্ধ পরিবেশ বজায় রাখা।",
                "ধাপ ৪: রাত ১০:৩০ এ প্রতিটি সাব-গ্রুপের রুম ইন-চার্জের উপস্থিতিতে হেডকাউন্ট যাচাই করে রুম লক করা।"
            ),
            checklistBn = listOf(
                "ওয়াশরুমে ২৪ ঘণ্টা অবিচ্ছিন্ন পানি সরবরাহ সচল আছে কি?",
                "প্রতিটি কক্ষে ফ্যান, লাইট ও চার্জিং পয়েন্ট সচল আছে কি?",
                "রুমভিত্তিক ফার্স্ট এইড ও মশার কয়েল প্রস্তুত আছে কি?",
                "হারিয়ে যাওয়া জিনিসপত্র সেন্ট্রাল লস্ট অ্যান্ড ফাউন্ডে জমা দেওয়া হয়েছে কি?"
            ),
            iconKey = "room"
        ),
        DepartmentSop(
            id = 5,
            key = "HEALTH",
            nameBn = "৫. স্বাস্থ্য, প্রাথমিক চিকিৎসা ও ক্যাম্প ক্লিনিক বিভাগ",
            nameEn = "5. Health, First Aid & Emergency Clinic",
            inChargeName = "ডা. তারিকুল ইসলাম (চিফ মেডিকেল অফিসার)",
            inChargePhone = "01819-222333",
            assistantName = "মো. আরিফুর রহমান (মেডিকেল অ্যাসিস্ট্যান্ট)",
            location = "ক্যাম্প ক্লিনিক ও অ্যাম্বুলেন্স বে (প্রশাসনিক ভবন সংলগ্ন)",
            shiftTime = "২৪ ঘণ্টা সার্বক্ষণিক জরুরি সেবা",
            coreObjectiveBn = "অংশগ্রহণকারীদের সার্বিক স্বাস্থ্য রক্ষা, অসুস্থদের তাৎক্ষণিক চিকিৎসা এবং জরুরি রেফারেল ব্যবস্থাপনা।",
            responsibilitiesBn = listOf(
                "ক্লিনিকে সার্বক্ষণিক ডাক্তার, ওষুধ ও ফার্স্ট এইড সামগ্রীর মজুদ রাখা।",
                "অ্যাজমা, শ্বাসকষ্ট, অ্যালার্জি ও ক্রনিক রোগাক্রান্ত শিক্ষার্থীদের বিশেষ নজরদারিতে রাখা।",
                "সকল প্রশিক্ষণার্থীর সাধারণ স্বাস্থ্য, দৃষ্টিশক্তি ও ডেন্টাল চেকআপ সম্পন্ন করে কার্ড আপডেট করা।",
                "গুরুতর অসুস্থতার ক্ষেত্রে উপজেলা/জেলা সদর হাসপাতালে পাঠানোর জন্য অ্যাম্বুলেন্স প্রস্তুত রাখা।"
            ),
            sopStepsBn = listOf(
                "ধাপ ১: কোনো শিক্ষার্থী অসুস্থ বোধ করলে সাব-গ্রুপ মেন্টর দ্রুত ক্লিনিকে নিয়ে আসবেন।",
                "ধাপ ২: ডাটাবেজে কিউআর কোড স্ক্যান করে পূর্ববর্তী হিস্ট্রি দেখে প্রেসক্রিপশন ও ওষুধ প্রদান করা।",
                "ধাপ ৩: হাই-প্রায়োরিটি বা সংক্রামক উপসর্গ দেখা দিলে আইসোলেশন বেডে শিফট করে মনিটরিংয়ে রাখা।",
                "ধাপ ৪: অভিভাবককে তাৎক্ষণিক ফোনে অবহিত করা এবং ফলো-আপ শিডিউল নির্ধারণ করা।"
            ),
            checklistBn = listOf(
                "অ্যাম্বুলেন্সের জ্বালানি ও অক্সিজেন সিলিন্ডার ফুল আছে কি?",
                "জরুরি ওআরএস, প্যারাসিটামল, অ্যান্টিহিস্টামিন ও ইনহেলার পর্যাপ্ত কি?",
                "বিপি মেশিন, থার্মোমিটার ও নেবুলাইজার চার্জড আছে কি?",
                "আইসোলেশন বেড প্রস্তুত ও পরিচ্ছন্ন আছে কি?"
            ),
            iconKey = "medical"
        ),
        DepartmentSop(
            id = 6,
            key = "CURRICULUM",
            nameBn = "৬. স্টেশন রোটেশন, সিলেবাস ও কারিকুলাম মনিটরিং বিভাগ",
            nameEn = "6. Station Rotation & Curriculum Quality",
            inChargeName = "উস্তাদ মাহমুদুল হাসান",
            inChargePhone = "01711-234567",
            assistantName = "ব্রাদার তানভীর হোসাইন",
            location = "একাডেমিক উইং ও স্টেশন মনিটরিং সেল",
            shiftTime = "সকাল ০৭:০০ - রাত ০৯:০০",
            coreObjectiveBn = "৬টি জীবন দক্ষতা স্টেশনের রোটেশন টাইমলাইন, সিলেবাস ও সক্রিয় শিখনফল শতভাগ বাস্তবায়ন।",
            responsibilitiesBn = listOf(
                "স্টেশন ১ থেকে ৬ এর মডারেটরদের সাথে সেশন শুরুর পূর্বে কারিকুলাম উপকরণ প্রস্তুত নিশ্চিত করা।",
                "রোটেশন ম্যাট্রিক্স অনুযায়ী প্রতিটি গ্রুপ নির্ধারিত স্টেশনে যথাসময়ে পৌঁছানো তদারকি করা।",
                "সেশন চলাকালে প্রশিক্ষণার্থীদের সক্রিয় অংশগ্রহণ, নোট গ্রহণ ও শিখনফল মূল্যায়ন করা।",
                "স্টেশন শেষে প্রশিক্ষণার্থী উপস্থিতি ও স্কোর ডাটাবেজে তাৎক্ষণিক এন্ট্রি নিশ্চিত করা।"
            ),
            sopStepsBn = listOf(
                "ধাপ ১: সেশন শুরুর ২০ মিনিট পূর্বে প্রতিটি স্টেশনে হোয়াইটবোর্ড, মার্কার ও হ্যান্ডআউট পৌঁছানো।",
                "ধাপ ২: বেল/সাইরেন বাজানোর সাথে সাথে ৫ মিনিটের মধ্যে দলগুলোর ইন্টার-স্টেশন মুভমেন্ট সম্পন্ন করা।",
                "ধাপ ৩: মডারেটর অনুপস্থিত থাকলে ব্যাকআপ প্রশিক্ষক দিয়ে তাৎক্ষণিক সেশন শুরু করা।",
                "ধাপ ৪: সেশন শেষে মডারেটরদের কাছ থেকে সেরা পারফর্মারদের নাম ও স্টেশন ফিডব্যাক সংগ্রহ করা।"
            ),
            checklistBn = listOf(
                "৬টি স্টেশনের মাইক, প্রজেক্টর ও ব্যবহারিক উপকরণ সচল আছে কি?",
                "প্রতিটি স্টেশনে পানীয় জলের জার ও বসার ফ্লোরম্যাট প্রস্তুত কি?",
                "রোটেশন ম্যাট্রিক্স চার্ট সকল স্টেশনে ঝুলানো আছে কি?",
                "স্টেশন অ্যাটেনডেন্স স্ক্যানার লগবুক আপডেট হয়েছে কি?"
            ),
            iconKey = "station"
        ),
        DepartmentSop(
            id = 7,
            key = "COMPETITIONS",
            nameBn = "৭. প্রতিযোগিতা, মূল্যায়ন ও বিচারকমণ্ডলী বিভাগ",
            nameEn = "7. Competitions, Scoring & Jury Panel",
            inChargeName = "মাওলানা আব্দুল গাফফার",
            inChargePhone = "01912-456789",
            assistantName = "প্রফেসর ড. আমিনুল ইসলাম",
            location = "সেন্ট্রাল অডিটোরিয়াম বিচারক ডেস্ক",
            shiftTime = "প্রতিযোগিতা শিডিউল অনুযায়ী",
            coreObjectiveBn = "ক্যাম্পের সকল প্রতিযোগিতার সুষ্ঠু, নিরপেক্ষ ও আন্তর্জাতিক মানের মূল্যায়ন পরিচালনা।",
            responsibilitiesBn = listOf(
                "ক্বিরাত, হিফজুল হাদিস, কুইজ, ক্যালিগ্রাফি, বক্তৃতা ও স্পোর্টস প্রতিযোগিতার শিডিউল পরিচালনা।",
                "বিশিষ্ট বিচারকদের অভ্যর্থনা, ব্রিফিং ও ক্যাটাগরিভিত্তিক স্কোরশিট হস্তান্তর।",
                "বিশুদ্ধতা (৪০), উপস্থাপনা (৩০) ও শৃঙ্খলা (৩০) এর ১০০ নম্বরের ভিত্তিতে নিরপেক্ষ স্কোর ইনপুট নিশ্চিত করা।",
                "স্বয়ংক্রিয় লিডারবোর্ড হালনাগাদ ও টাইব্রেকিং নিয়মাবলি প্রয়োগ করা।"
            ),
            sopStepsBn = listOf(
                "ধাপ ১: ইভেন্ট শুরুর ৩০ মিনিট পূর্বে বিচারকদের মঞ্চে আসন গ্রহণ ও স্কোরিং ক্রাইটেরিয়া ব্রিফিং।",
                "ধাপ ২: অংশগ্রহণকারীদের রোল নম্বর ধরে নাম ঘোষণা এবং সময়সীমক (টাইমার) বজায় রাখা।",
                "ধাপ ৩: প্রতিটি পারফরম্যান্স শেষে বিচারকদের সইযুক্ত স্কোর ডিজিটাল অ্যাপে এন্ট্রি ও লক করা।",
                "ধাপ ৪: প্রতিযোগিতা সমাপ্তির পর সিলগালা করা রেজাল্ট শিট ক্যাম্প চিফের নিকট হস্তান্তর করা।"
            ),
            checklistBn = listOf(
                "প্রতিযোগীদের লটারির টোকেন ও অংশগ্রহণ তালিকা প্রস্তুত আছে কি?",
                "বিচারকদের ট্যাবলেটে ডিজিটাল স্কোরিং মডিউল আনলক আছে কি?",
                "অডিটোরিয়ামের সাউন্ড সিস্টেম, বেল ও স্টপওয়াচ কার্যকর আছে কি?",
                "পুরস্কার ও সার্টিফিকেটের রেজাল্ট ভেরিফিকেশন সম্পন্ন কি?"
            ),
            iconKey = "competition"
        ),
        DepartmentSop(
            id = 8,
            key = "PUBLICATIONS",
            nameBn = "৮. প্রকাশনা, দেয়াল পত্রিকা ও গণমাধ্যম বিভাগ",
            nameEn = "8. Publications, Wall Magazine & Media Relations",
            inChargeName = "মাহবুব মোরশেদ",
            inChargePhone = "01618-111222",
            assistantName = "মুহাম্মদ আল-আমিন",
            location = "মিডিয়া সেন্টার ও প্রকাশনা কর্নার",
            shiftTime = "সকাল ০৭:০০ - রাত ১০:০০",
            coreObjectiveBn = "ক্যাম্পের সৃজনশীল সাহিত্য প্রকাশ, দেয়াল পত্রিকা মূল্যায়ন ও ইতিবাচক প্রচার নিশ্চিত করা।",
            responsibilitiesBn = listOf(
                "প্রতিদিনের দেয়াল পত্রিকার লেখা, ক্যালিগ্রাফি ও চিত্রাঙ্কন সংগ্রহ এবং প্রদর্শন।",
                "প্রতিদিন সন্ধ্যায় 'ক্যাম্প বন্ধু লাইভ বুলেটিন' ড্রাফট করে অনুমোদন ও পাবলিশ করা।",
                "ক্যাম্পের গুরুত্বপূর্ণ ইভেন্ট, ড্রিল ও প্রতিযোগিতার উচ্চমানের ফটো ও ভিডিও ডকুমেন্টেশন।",
                "সোশ্যাল মিডিয়া ও প্রেস রিলিজের জন্য ক্যাম্পের শিক্ষণীয় মুহূর্তগুলো সংকলন করা।"
            ),
            sopStepsBn = listOf(
                "ধাপ ১: দুপুর ১২:০০ টার মধ্যে ৬টি গ্রুপ থেকে দেয়াল পত্রিকার কনটেন্ট সংগ্রহ করা।",
                "ধাপ ২: প্রকাশনা প্যানেল কর্তৃক বিষয়বস্তু, বানান ও শিল্পমান নিরীক্ষা করে সেরা ৩টি লেখা বাছাই।",
                "ধাপ ৩: বিকাল ০৫:০০ টায় প্রধান দেয়ালিকা বোর্ডে নির্বাচিত দেয়াল পত্রিকা স্থাপন করা।",
                "ধাপ ৪: ক্যাম্পফায়ার সেশনের শুরুতে দিনের সেরা বুলেটিন ও আলোকচিত্র মাল্টিমিডিয়ায় প্রদর্শন করা।"
            ),
            checklistBn = listOf(
                "দেয়াল পত্রিকার ড্রয়িং পেপার, মার্কার ও পিন পর্যাপ্ত আছে কি?",
                "ডিএসএলআর ক্যামেরা ব্যাটারি ও মেমোরি কার্ড ব্যাকআপ প্রস্তুত কি?",
                "লাইভ বুলেটিন সিস্টেমে নতুন নোটিশ ড্রাফট করা আছে কি?",
                "ছবি ব্যবহারের ক্ষেত্রে চাইল্ড প্রোটেকশন ও অভিভাবক সম্মতি যাচাই হয়েছে কি?"
            ),
            iconKey = "bulletin"
        ),
        DepartmentSop(
            id = 9,
            key = "LOGISTICS",
            nameBn = "৯. লজিস্টিকস, সামগ্রী বিতরণ ও কিট ইনভেন্টরি বিভাগ",
            nameEn = "9. Logistics, Kit Distribution & Awards Stock",
            inChargeName = "ইঞ্জিনিয়ার রেজওয়ান করিম",
            inChargePhone = "01611-444555",
            assistantName = "মো. মনির হোসেন",
            location = "সেন্ট্রাল স্টোর ও ডিস্ট্রিবিউশন হাব",
            shiftTime = "সকাল ০৬:০০ - রাত ১০:০০",
            coreObjectiveBn = "ক্যাম্পের সকল প্রয়োজনীয় মালামাল, কিট ও পুরস্কার সামগ্রীর সুরক্ষিত মজুদ ও সঠিক বিতরণ।",
            responsibilitiesBn = listOf(
                "অংশগ্রহণকারীদের কিট ব্যাগ, ক্যাপ, জার্সি, খাতা, কলম ও আইডি ব্যাজ শতভাগ বিতরণ নিশ্চিত করা।",
                "ট্রফি, মেডেল, ক্রেস্ট, কুরআন শরীফ ও বই উপহারের দৈনিক স্টক ট্র্যাকিং করা।",
                "স্টেশন ও অডিটোরিয়ামে প্রয়োজনীয় স্টেশনারি ও ইকুইপমেন্ট সঠিক সময়ে পৌঁছে দেওয়া।",
                "স্টক ঘাটতি দেখা দেওয়ার পূর্বেই রিকুইজিশন তৈরি করে সেন্ট্রাল পারচেজে প্রেরণ।"
            ),
            sopStepsBn = listOf(
                "ধাপ ১: স্টোরে সকল মালামাল শ্রেণিভুক্ত করে নির্দিষ্ট সেলফে রাখা ও স্টক লেবেল লাগানো।",
                "ধাপ ২: যেকোনো আইটেম ইস্যু করার সময় রিসিভারের ক্যাম্প কোড ও স্বাক্ষর ভাউচারে নেওয়া।",
                "ধাপ ৩: দিনের শেষে ডিজিটাল ইনভেন্টরি মডিউলে অপেনিং, ইস্যু ও ব্যালেন্স সংখ্যা মেলানো।",
                "ধাপ ৪: কোনো সামগ্রী ক্ষতিগ্রস্থ হলে ড্যামেজ রেজিস্টারে কারণসহ লিপিবদ্ধ করা।"
            ),
            checklistBn = listOf(
                "২৮টি ইনভেন্টরি আইটেমের ফিজিক্যাল ব্যালেন্স সফটওয়্যারের সাথে মিলছে কি?",
                "সমাপনী অনুষ্ঠানের মেডেল ও ক্রেস্ট প্যাকেট প্রস্তুত আছে কি?",
                "জরুরি অতিরিক্ত ব্যাজ ও জার্সি স্টোরে সংরক্ষিত আছে কি?",
                "স্টোর রুমে ফায়ার সেফটি ও শুকনা পরিবেশ নিশ্চিত কি?"
            ),
            iconKey = "inventory"
        ),
        DepartmentSop(
            id = 10,
            key = "TRANSPORT",
            nameBn = "১০. পরিবহন, শিক্ষা সফর ও ভ্রমণ নিরাপত্তা বিভাগ",
            nameEn = "10. Transport, Study Tour & Excursion",
            inChargeName = "মো. শফিকুল ইসলাম",
            inChargePhone = "01712-888006",
            assistantName = "মো. বাবুল হোসেন (যানবাহন সুপারভাইজার)",
            location = "বাস টার্মিনাল ও পরিবহন সেল",
            shiftTime = "সফরকালীন সার্বক্ষণিক",
            coreObjectiveBn = "শিক্ষা সফরে ৬০৩ জন শিক্ষার্থী ও কর্মকর্তাদের শতভাগ নিরাপদ ও সুশৃঙ্খল পরিবহন ব্যবস্থা।",
            responsibilitiesBn = listOf(
                "৬টি সংরক্ষিত বাস (ঢাকা মেট্রো ব-১১-২৩৪৫ থেকে ২৩৫০) এর ফিটনেস ও চালকদের ব্রিফিং।",
                "গ্রুপ ভিত্তিক বাস বরাদ্দ ও শিক্ষার্থীদের আসন বণ্টন কার্ড বিতরণ।",
                "রওয়ানা হওয়ার আগে ও ক্যাম্পাসে ফিরে আসার পর ১০০% নির্ভুল হেডকাউন্ট নিশ্চিত করা।",
                "প্রতিটি বাসে সাব-গ্রুপ ইন-চার্জ, ফার্স্ট এইড কিট, শুকনা খাবার ও বিশুদ্ধ পানি নিশ্চিত করা।"
            ),
            sopStepsBn = listOf(
                "ধাপ ১: সফরের দিন সকাল ০৬:৩০ টায় সকল বাসের ইঞ্জিন, ব্রেক, টায়ার ও জ্বালানি পরীক্ষা সম্পন্ন করা।",
                "ধাপ ২: সকাল ০৭:১৫ টায় চালক ও সহকারীদের ট্রাফিক নিয়ম ও ওভারস্পিডিং নিষিদ্ধের লিখিত ব্রিফিং।",
                "ধাপ ৩: বোর্ডিংয়ের সময় কিউআর স্ক্যান করে ডিপার্চার হেডকাউন্ট রেজিস্টারে এন্ট্রি নেওয়া।",
                "ধাপ ৪: গন্তব্যে পৌঁছার পর এবং ক্যাম্পাসে প্রত্যাবর্তনের সময় পুনরায় হেডকাউন্ট মিলিয়ে বাস ছাড়পত্র দেওয়া।"
            ),
            checklistBn = listOf(
                "৬টি বাসের চালকদের ড্রাইভিং লাইসেন্স ও জরুরি ফোন নম্বর রেজিস্টারে আছে কি?",
                "প্রতিটি বাসের সামনে সংশ্লিষ্ট দলের নাম ও বাস নম্বর স্টিকার লাগানো কি?",
                "জরুরি পরিস্থিতিতে পুলিশ ও স্থানীয় প্রশাসনের হেল্পলাইন ড্রাইভারদের দেওয়া আছে কি?",
                "সফরের দর্শনীয় স্থানের অনুমতিপত্র ও গাইড ম্যাপ সাথে রাখা হয়েছে কি?"
            ),
            iconKey = "transport"
        ),
        DepartmentSop(
            id = 11,
            key = "ACCOUNTS",
            nameBn = "১১. হিসাব, বাজেট ও অর্থ নিরীক্ষা বিভাগ",
            nameEn = "11. Finance, Accounts & Daily Audit",
            inChargeName = "মুহাম্মদ তারিক রহমান",
            inChargePhone = "01811-777888",
            assistantName = "মো. কামরুল হাসান (অ্যাকাউন্ট্যান্ট)",
            location = "ফিন্যান্স ও অ্যাকাউন্টস ডেস্ক, এডমিন উইং",
            shiftTime = "সকাল ০৯:০০ - রাত ০৯:০০",
            coreObjectiveBn = "ক্যাম্পের সার্বিক আর্থিক লেনদেনের স্বচ্ছতা, বাজেট নিয়ন্ত্রণ ও দৈনিক অডিট নিশ্চিত করা।",
            responsibilitiesBn = listOf(
                "ক্যাম্পের সকল অনুমোদনপ্রাপ্ত খরচের ভাউচার, ক্যাশ মেমো ও বিল যাচাই-বাছাই করা।",
                "ক্যাটারিং, লজিস্টিকস, পরিবহন ও জরুরি খরচের বিপরীতে ক্যাশ ইন হ্যান্ড প্রদান।",
                "প্রতিদিন রাত ১০:০০ টায় দিনের যাবতীয় ব্যয়ের হিসাব চূড়ান্ত করে ক্যাশ বুক মেলানো।",
                "ক্যাম্প চিফের নিকট দৈনিক আর্থিক বিবরণী ও ব্যালেন্স রিপোর্ট পেশ করা।"
            ),
            sopStepsBn = listOf(
                "ধাপ ১: কোনো বিভাগ থেকে অর্থ দাবি আসলে ক্যাম্প চিফের লিখিত অনুমোদন আছে কিনা যাচাই করা।",
                "ধাপ ২: বিল বা ক্যাশ মেমোতে ক্রয়ের বিবরণ, পরিমাণ ও প্রাপকের স্বাক্ষর নিশ্চিত করা।",
                "ধাপ ৩: ডিজিটাল সিস্টেমে প্রতিটি ভাউচার এন্ট্রি করে ইউনিক ভাউচার নম্বর বসানো।",
                "ধাপ ৪: বড় অংকের পেমেন্ট সরাসরি ব্যাংক ট্রান্সফার বা চেকের মাধ্যমে সম্পন্ন করা।"
            ),
            checklistBn = listOf(
                "দৈনিক খরচের প্রতিটি ভাউচারে সংশ্লিষ্ট বিভাগীয় প্রধানের স্বাক্ষর আছে কি?",
                "জরুরি আপদকালীন পেটি ক্যাশ ড্রয়ারে প্রস্তুত আছে কি?",
                "ক্যাটারিং মাংস ও সবজি ক্রয়ের ওজন ও মূল্য তালিকা মেলানো হয়েছে কি?",
                "অডিট রেজিস্টারে কোনো অসমাপ্ত সমন্বয় পেন্ডিং আছে কি?"
            ),
            iconKey = "accounts"
        ),
        DepartmentSop(
            id = 12,
            key = "REGISTRATION",
            nameBn = "১২. রেজিস্ট্রেশন, অভ্যর্থনা ও ডিজিটাল সাপোর্ট সেল",
            nameEn = "12. Registration, Reception & IT Helpdesk",
            inChargeName = "মুহাম্মদ ফারহান কবির",
            inChargePhone = "01811-123456",
            assistantName = "আহমেদ তানজিম (আইটি অফিসার)",
            location = "প্রধান প্রবেশদ্বার অভ্যর্থনা ডেস্ক ও সার্ভার রুম",
            shiftTime = "সার্বক্ষণিক",
            coreObjectiveBn = "অংশগ্রহণকারীদের দ্রুত আগমনী অভ্যর্থনা, ডাটাবেজ যাচাই, ডিজিটাল আইডি প্রদান ও সার্বিক আইটি সহায়তা।",
            responsibilitiesBn = listOf(
                "৬৪ জেলা থেকে আগত শিক্ষার্থীদের রেজিস্ট্রেশন ফাইল নম্বর ও ক্যাম্প কোড যাচাই করা।",
                "স্মার্ট ল্যামিনেটেড কিউআর আইডি কার্ড ও ক্যাম্প হ্যান্ডবুক শিক্ষার্থীদের বুঝিয়ে দেওয়া।",
                "ক্যাম্প কমপ্যানিয়ন অ্যাপের লোকাল ডেটাবেজ, অফলাইন সিঙ্ক ও ব্যাকআপ তদারকি করা।",
                "অভিভাবকদের যোগাযোগের জন্য তথ্য কেন্দ্র ও হেল্পলাইন ডেস্ক পরিচালনা করা।"
            ),
            sopStepsBn = listOf(
                "ধাপ ১: শিক্ষার্থী গেটে আসার সাথে সাথে ঠান্ডা পানীয় দিয়ে অভ্যর্থনা জানানো ও কিউতে দাঁড় করানো।",
                "ধাপ ২: ডাটাবেজে নাম ও ছবি মিলিয়ে কিউআর কোড স্ক্যান করে উপস্থিতি নিশ্চিত করা।",
                "ধাপ ৩: শিক্ষার্থীর দল ও রুম নম্বর নির্দেশ করে ভলান্টিয়ারের মাধ্যমে রুমে পৌঁছে দেওয়া।",
                "ধাপ ৪: প্রতি রাতে ডেটাবেজের ব্যাকআপ এক্সপোর্ট নিয়ে ক্লাউড ও পেনড্রাইভে সংরক্ষণ করা।"
            ),
            checklistBn = listOf(
                "স্মার্ট আইডি কার্ডের কিউআর কোড স্ক্যানারে সঠিকভাবে রিড করছে কি?",
                "অংশগ্রহণকারী ডাটাবেজে রক্তের গ্রুপ ও অভিভাবকের ফোন নম্বর নির্ভুল কি?",
                "আইটি হেল্পডেস্কে অতিরিক্ত প্রিন্টার কালি ও ল্যামিনেশন পেপার আছে কি?",
                "রুম ও ডেটাবেজ ব্যাকআপ সম্পন্ন হয়েছে কি?"
            ),
            iconKey = "helpdesk"
        )
    )

    // Complete 24 Subgroups from Page 27 of LSC 2025 Rationale
    val subgroups = listOf(
        // Group 1: বুখারী (Bukhari)
        SubgroupInfo(
            id = "bukhari_sub1",
            groupId = "bukhari",
            groupNameBn = "বুখারী",
            groupNameEn = "Bukhari",
            subgroupNo = 1,
            subgroupCode = "বুখারী সাব-১ (Bukhari-1)",
            mentorName = "মুহাম্মদ জুবায়ের",
            mentorPhone = "01711-101001",
            roomNo = "১০১",
            buildingName = "তাজমহল হল (১ম তলা)",
            memberCount = 25,
            colorHex = 0xFF1976D2
        ),
        SubgroupInfo(
            id = "bukhari_sub2",
            groupId = "bukhari",
            groupNameBn = "বুখারী",
            groupNameEn = "Bukhari",
            subgroupNo = 2,
            subgroupCode = "বুখারী সাব-২ (Bukhari-2)",
            mentorName = "হাফেজ তানভীর আহমেদ (দলনেতা)",
            mentorPhone = "01711-234567",
            roomNo = "১০১",
            buildingName = "তাজমহল হল (১ম তলা)",
            memberCount = 25,
            colorHex = 0xFF1976D2
        ),
        SubgroupInfo(
            id = "bukhari_sub3",
            groupId = "bukhari",
            groupNameBn = "বুখারী",
            groupNameEn = "Bukhari",
            subgroupNo = 3,
            subgroupCode = "বুখারী সাব-৩ (Bukhari-3)",
            mentorName = "আহমেদ হাসিব",
            mentorPhone = "01711-101002",
            roomNo = "১০২",
            buildingName = "তাজমহল হল (১ম তলা)",
            memberCount = 25,
            colorHex = 0xFF1976D2
        ),
        SubgroupInfo(
            id = "bukhari_sub4",
            groupId = "bukhari",
            groupNameBn = "বুখারী",
            groupNameEn = "Bukhari",
            subgroupNo = 4,
            subgroupCode = "বুখারী সাব-৪ (Bukhari-4)",
            mentorName = "মাহমুদুর রহমান",
            mentorPhone = "01711-101007",
            roomNo = "১০২",
            buildingName = "তাজমহল হল (১ম তলা)",
            memberCount = 26,
            colorHex = 0xFF1976D2
        ),

        // Group 2: সুমাইয়া (Sumaiya)
        SubgroupInfo(
            id = "sumaiya_sub1",
            groupId = "sumaiya",
            groupNameBn = "সুমাইয়া",
            groupNameEn = "Sumaiya",
            subgroupNo = 1,
            subgroupCode = "সুমাইয়া সাব-১ (Sumaiya-1)",
            mentorName = "ফারহানা ইয়াসমিন",
            mentorPhone = "01819-101003",
            roomNo = "১০৩",
            buildingName = "তাজমহল হল (১ম তলা)",
            memberCount = 25,
            colorHex = 0xFFC2185B
        ),
        SubgroupInfo(
            id = "sumaiya_sub2",
            groupId = "sumaiya",
            groupNameBn = "সুমাইয়া",
            groupNameEn = "Sumaiya",
            subgroupNo = 2,
            subgroupCode = "সুমাইয়া সাব-২ (Sumaiya-2)",
            mentorName = "মুহতারামা খাদিজা আক্তার (দলনেতা)",
            mentorPhone = "01819-345678",
            roomNo = "১০৩",
            buildingName = "তাজমহল হল (১ম তলা)",
            memberCount = 25,
            colorHex = 0xFFC2185B
        ),
        SubgroupInfo(
            id = "sumaiya_sub3",
            groupId = "sumaiya",
            groupNameBn = "সুমাইয়া",
            groupNameEn = "Sumaiya",
            subgroupNo = 3,
            subgroupCode = "সুমাইয়া সাব-৩ (Sumaiya-3)",
            mentorName = "রুমানা আফরোজ",
            mentorPhone = "01819-101004",
            roomNo = "১০৪",
            buildingName = "তাজমহল হল (১ম তলা)",
            memberCount = 25,
            colorHex = 0xFFC2185B
        ),
        SubgroupInfo(
            id = "sumaiya_sub4",
            groupId = "sumaiya",
            groupNameBn = "সুমাইয়া",
            groupNameEn = "Sumaiya",
            subgroupNo = 4,
            subgroupCode = "সুমাইয়া সাব-৪ (Sumaiya-4)",
            mentorName = "সাদিয়া আফরিন",
            mentorPhone = "01819-101008",
            roomNo = "১০৪",
            buildingName = "তাজমহল হল (১ম তলা)",
            memberCount = 25,
            colorHex = 0xFFC2185B
        ),

        // Group 3: শাফেয়ী (Shafeyi)
        SubgroupInfo(
            id = "shafeyi_sub1",
            groupId = "shafeyi",
            groupNameBn = "শাফেয়ী",
            groupNameEn = "Shafeyi",
            subgroupNo = 1,
            subgroupCode = "শাফেয়ী সাব-১ (Shafeyi-1)",
            mentorName = "মাহমুদুর রহমান সোহাগ",
            mentorPhone = "01912-101005",
            roomNo = "১০৫",
            buildingName = "তাজমহল হল (১ম তলা)",
            memberCount = 25,
            colorHex = 0xFF00796B
        ),
        SubgroupInfo(
            id = "shafeyi_sub2",
            groupId = "shafeyi",
            groupNameBn = "শাফেয়ী",
            groupNameEn = "Shafeyi",
            subgroupNo = 2,
            subgroupCode = "শাফেয়ী সাব-২ (Shafeyi-2)",
            mentorName = "মাওলানা আব্দুল গাফফার (দলনেতা)",
            mentorPhone = "01912-456789",
            roomNo = "১০৫",
            buildingName = "তাজমহল হল (১ম তলা)",
            memberCount = 25,
            colorHex = 0xFF00796B
        ),
        SubgroupInfo(
            id = "shafeyi_sub3",
            groupId = "shafeyi",
            groupNameBn = "শাফেয়ী",
            groupNameEn = "Shafeyi",
            subgroupNo = 3,
            subgroupCode = "শাফেয়ী সাব-৩ (Shafeyi-3)",
            mentorName = "সাঈদ আনোয়ার",
            mentorPhone = "01912-101006",
            roomNo = "১০৬",
            buildingName = "তাজমহল হল (১ম তলা)",
            memberCount = 25,
            colorHex = 0xFF00796B
        ),
        SubgroupInfo(
            id = "shafeyi_sub4",
            groupId = "shafeyi",
            groupNameBn = "শাফেয়ী",
            groupNameEn = "Shafeyi",
            subgroupNo = 4,
            subgroupCode = "শাফেয়ী সাব-৪ (Shafeyi-4)",
            mentorName = "তারেক মাহমুদ",
            mentorPhone = "01912-101009",
            roomNo = "১০৬",
            buildingName = "তাজমহল হল (১ম তলা)",
            memberCount = 26,
            colorHex = 0xFF00796B
        ),

        // Group 4: উম্মে আয়মান (Umm Ayman)
        SubgroupInfo(
            id = "umm_ayman_sub1",
            groupId = "umm_ayman",
            groupNameBn = "উম্মে আয়মান",
            groupNameEn = "Umm Ayman",
            subgroupNo = 1,
            subgroupCode = "উম্মে আয়মান সাব-১ (Umm Ayman-1)",
            mentorName = "সাবিহা পারভীন",
            mentorPhone = "01722-201001",
            roomNo = "২০১",
            buildingName = "শহীদ মিনার ভবন (২য় তলা)",
            memberCount = 25,
            colorHex = 0xFF7B1FA2
        ),
        SubgroupInfo(
            id = "umm_ayman_sub2",
            groupId = "umm_ayman",
            groupNameBn = "উম্মে আয়মান",
            groupNameEn = "Umm Ayman",
            subgroupNo = 2,
            subgroupCode = "উম্মে আয়মান সাব-২ (Umm Ayman-2)",
            mentorName = "মুহতারামা সাবিহা পারভীন (দলনেতা)",
            mentorPhone = "01722-567890",
            roomNo = "২০১",
            buildingName = "শহীদ মিনার ভবন (২য় তলা)",
            memberCount = 25,
            colorHex = 0xFF7B1FA2
        ),
        SubgroupInfo(
            id = "umm_ayman_sub3",
            groupId = "umm_ayman",
            groupNameBn = "উম্মে আয়মান",
            groupNameEn = "Umm Ayman",
            subgroupNo = 3,
            subgroupCode = "উম্মে আয়মান সাব-৩ (Umm Ayman-3)",
            mentorName = "নুসরাত জাহান",
            mentorPhone = "01722-201002",
            roomNo = "২০২",
            buildingName = "শহীদ মিনার ভবন (২য় তলা)",
            memberCount = 25,
            colorHex = 0xFF7B1FA2
        ),
        SubgroupInfo(
            id = "umm_ayman_sub4",
            groupId = "umm_ayman",
            groupNameBn = "উম্মে আয়মান",
            groupNameEn = "Umm Ayman",
            subgroupNo = 4,
            subgroupCode = "উম্মে আয়মান সাব-৪ (Umm Ayman-4)",
            mentorName = "মারিয়াম সুলতানা",
            mentorPhone = "01722-201008",
            roomNo = "২০২",
            buildingName = "শহীদ মিনার ভবন (২য় তলা)",
            memberCount = 25,
            colorHex = 0xFF7B1FA2
        ),

        // Group 5: সালাহউদ্দীন আইয়ুবী (Salahuddin Ayyubi)
        SubgroupInfo(
            id = "salahuddin_sub1",
            groupId = "salahuddin",
            groupNameBn = "সালাহউদ্দীন আইয়ুবী",
            groupNameEn = "Salahuddin Ayyubi",
            subgroupNo = 1,
            subgroupCode = "সালাহউদ্দীন সাব-১ (Salahuddin-1)",
            mentorName = "রায়হান উদ্দিন",
            mentorPhone = "01611-201003",
            roomNo = "২০৩",
            buildingName = "শহীদ মিনার ভবন (২য় তলা)",
            memberCount = 25,
            colorHex = 0xFFE65100
        ),
        SubgroupInfo(
            id = "salahuddin_sub2",
            groupId = "salahuddin",
            groupNameBn = "সালাহউদ্দীন আইয়ুবী",
            groupNameEn = "Salahuddin Ayyubi",
            subgroupNo = 2,
            subgroupCode = "সালাহউদ্দীন সাব-২ (Salahuddin-2)",
            mentorName = "ইঞ্জিনিয়ার রায়হান উদ্দিন (দলনেতা)",
            mentorPhone = "01611-678901",
            roomNo = "২০৩",
            buildingName = "শহীদ মিনার ভবন (২য় তলা)",
            memberCount = 25,
            colorHex = 0xFFE65100
        ),
        SubgroupInfo(
            id = "salahuddin_sub3",
            groupId = "salahuddin",
            groupNameBn = "সালাহউদ্দীন আইয়ুবী",
            groupNameEn = "Salahuddin Ayyubi",
            subgroupNo = 3,
            subgroupCode = "সালাহউদ্দীন সাব-৩ (Salahuddin-3)",
            mentorName = "তারেক মাহমুদ",
            mentorPhone = "01611-201004",
            roomNo = "২০৪",
            buildingName = "শহীদ মিনার ভবন (২য় তলা)",
            memberCount = 25,
            colorHex = 0xFFE65100
        ),
        SubgroupInfo(
            id = "salahuddin_sub4",
            groupId = "salahuddin",
            groupNameBn = "সালাহউদ্দীন আইয়ুবী",
            groupNameEn = "Salahuddin Ayyubi",
            subgroupNo = 4,
            subgroupCode = "সালাহউদ্দীন সাব-৪ (Salahuddin-4)",
            mentorName = "কাউসার আহমেদ",
            mentorPhone = "01611-201009",
            roomNo = "২০৪",
            buildingName = "শহীদ মিনার ভবন (২য় তলা)",
            memberCount = 26,
            colorHex = 0xFFE65100
        ),

        // Group 6: উম্মে সা'দ (Umm Sa'ad)
        SubgroupInfo(
            id = "umm_saad_sub1",
            groupId = "umm_saad",
            groupNameBn = "উম্মে সা'দ",
            groupNameEn = "Umm Sa'ad",
            subgroupNo = 1,
            subgroupCode = "উম্মে সা'দ সাব-১ (Umm Sa'ad-1)",
            mentorName = "নাসরিন জাহান",
            mentorPhone = "01511-201005",
            roomNo = "২০৫",
            buildingName = "শহীদ মিনার ভবন (২য় তলা)",
            memberCount = 25,
            colorHex = 0xFF388E3C
        ),
        SubgroupInfo(
            id = "umm_saad_sub2",
            groupId = "umm_saad",
            groupNameBn = "উম্মে সা'দ",
            groupNameEn = "Umm Sa'ad",
            subgroupNo = 2,
            subgroupCode = "উম্মে সা'দ সাব-২ (Umm Sa'ad-2)",
            mentorName = "মুহতারামা নাসরিন জাহান (দলনেতা)",
            mentorPhone = "01511-789012",
            roomNo = "২০৫",
            buildingName = "শহীদ মিনার ভবন (২য় তলা)",
            memberCount = 25,
            colorHex = 0xFF388E3C
        ),
        SubgroupInfo(
            id = "umm_saad_sub3",
            groupId = "umm_saad",
            groupNameBn = "উম্মে সা'দ",
            groupNameEn = "Umm Sa'ad",
            subgroupNo = 3,
            subgroupCode = "উম্মে সা'দ সাব-৩ (Umm Sa'ad-3)",
            mentorName = "সালমা বেগম",
            mentorPhone = "01511-201006",
            roomNo = "২০৬",
            buildingName = "শহীদ মিনার ভবন (২য় তলা)",
            memberCount = 25,
            colorHex = 0xFF388E3C
        ),
        SubgroupInfo(
            id = "umm_saad_sub4",
            groupId = "umm_saad",
            groupNameBn = "উম্মে সা'দ",
            groupNameEn = "Umm Sa'ad",
            subgroupNo = 4,
            subgroupCode = "উম্মে সা'দ সাব-৪ (Umm Sa'ad-4)",
            mentorName = "হাবিবা খাতুন",
            mentorPhone = "01511-201008",
            roomNo = "২০৬",
            buildingName = "শহীদ মিনার ভবন (২য় তলা)",
            memberCount = 25,
            colorHex = 0xFF388E3C
        )
    )
}
