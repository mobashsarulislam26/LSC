package com.example.data.sample

import com.example.data.model.ParticipantEntity
import com.example.data.model.RoomEntity

/**
 * Official Life Skills Camping 2026 (WAMY Bangladesh) Master Roster & Room Allocation.
 * Extracted directly from the official 2026 Camp Master Document (Tables 26–57):
 * - 609 real orphan participants across 6 groups & 24 subgroups
 * - Exact Platinum 1–6 (Boys), Golden 1–7, and Rajonigandha/Silver (Girls) room & bed mappings
 */
object OfficialRoster2026 {

    private const val BUKHARI_TSV = """
1|Bukhari-1-1|Bagura-3|সোহান হাসান|Sohan Hasan|বগুড়া|Bogura|WAMY-OSP|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-A|Bed-01
2|Bukhari-1-2|SKB2614|মোহতাসিম মোজাহিদ মাহি|Mohtasim Mojahid Mahi|জয়পুরহাট|Joypurhat|SKB|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-A|Bed-02
3|Bukhari-1-3|EDAS-1258|মো. শাহরিয়ার মোস্তাকিম|Md. Shahriar Mostakim|নওগাঁ|Naogaon|EDAS|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-A|Bed-03
4|Bukhari-1-4|EDAS-791|আদনান আফিফ আবির|Adnan Afif Abir|পঞ্চগড়|Panchagarh|EDAS|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-A|Bed-04
5|Bukhari-1-5|EDAS-201|মো. জিয়াউর রহমান|Md Ziaur Rahman|পটুয়াখালী|Patuakhali|EDAS|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-A|Bed-05
6|Bukhari-1-6|N/A|রাকিবুল ইসলাম|Rakibul Islam|কিশোরগঞ্জ|Kishorganj|WAMY-OSP|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-A|Bed-06
7|Bukhari-1-7|Bagura-53|মো. জুনায়েদ আহমেদ তোহা|Md. Junaid Ahmed Toha|বগুড়া|Bogura|WAMY-OSP|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-A|Bed-07
8|Bukhari-1-8|EDAS-1013|মো. জুবায়ের রহমান|Md. Jubayer Rahman|দিনাজপুর|Dinajpur|EDAS|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-B|Bed-01
9|Bukhari-1-9|IAB0524|মো. রিয়াদ আহমেদ|Md. Riyad Ahmed|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-B|Bed-02
10|Bukhari-1-10|BD-31452|তাসকিন আহমদ নাহিদ|Taskin Ahamad Nahid|খুলনা (IAB)|IAB-Khulna|IAB|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-B|Bed-03
11|Bukhari-1-11|379|মিজান মিয়া|Mizan Miah|মুন্সীগঞ্জ (IAB)|IAB-Munshigonj|IAB|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-B|Bed-04
12|Bukhari-1-12|BD-0076|ইব্রাহিম হোসেন তানভীর|Ibrahim Hosen Tanvir|যশোর|Jashore|WAMY-BD|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-B|Bed-05
13|Bukhari-1-13|22100191|ওসমান গণি|Osman Gani|কুড়িগ্রাম|Kurigram|WAMY-OSP|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-B|Bed-06
14|Bukhari-1-14|EDAS-708|সাইফুল ইসলাম|Saiful Islam|লালমনিরহাট|Lalmonirhat|EDAS|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-B|Bed-07
15|Bukhari-1-15|Magura-11|মো. আলিফ ইসলাম|Md. Alif Islam|মাগুরা|Magura|WAMY-OSP|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-C|Bed-01
16|Bukhari-1-16|EDAS-1567|জুবায়েদ হোসেন|Jubayed Hossain|নরসিংদী|Narsingdi|EDAS|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-C|Bed-02
17|Bukhari-1-17|EDAS-1595|রাশেদ আহমেদ ইউসুফ|Rashed Ahmed Yousuf|নরসিংদী|Narsingdi|EDAS|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-C|Bed-03
18|Bukhari-1-18|SKB 2612|আবদুল্লাহ আল আরাফাত|Abdullah Al- Arafat|নীলফামারী|Nilphamari|SKB|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-C|Bed-04
19|Bukhari-1-19|EDAS-832|মো. মিজানুর রহমান|Md. Mizanur Rahman|পঞ্চগড়|Panchagarh|EDAS|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-C|Bed-05
20|Bukhari-1-20|EDAS- 1721|মিনারুল|Minarul|সাতক্ষীরা|Satkhira|EDAS|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-C|Bed-06
21|Bukhari-1-21|SKB 2372|মোহাম্মদ রাকিব|Mohammad Rakib|কক্সবাজার|Cox's Bazar|SKB|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-Common|Bed-01
22|Bukhari-1-22|30892|মো. আজমীর|Md Azmir|চাঁদপুর (IAB)|IAB-Chandpur|IAB|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-Common|Bed-02
23|Bukhari-1-23|BD-31467|সামাদুল ইসলাম|Samadul Islam|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-Common|Bed-03
24|Bukhari-1-24|31501|মো. সৈকত আহমেদ|Md. Soikot Ahmed|লালমনিরহাট (IAB)|IAB-Lalmonirhat|IAB|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-Common|Bed-04
25|Bukhari-1-25|411|রাহাত হোসেন|Rahat Hossen|মুন্সীগঞ্জ (IAB)|IAB-Munshigonj|IAB|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-Common|Bed-05
26|Bukhari-1-26|EDAS-1170|সাজ্জাদ হোসেন মিম|Sazzad Hosen Mim|জয়পুরহাট|Joypurhat|EDAS|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-Common|Bed-06
27|Bukhari-1-27|EDAS-1310|মো. আবদুল আলীম|Md. Abdul Aleem|কুষ্টিয়া|Kustia|EDAS|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-Common|Bed-07
28|Bukhari-1-28|Meherpur-15|মো. জুনায়েদ আলী|Md. Junayed Ali|মেহেরপুর|Meherpur|WAMY-OSP|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-Common|Bed-08
29|Bukhari-1-29|EDAS-1277|মো. আল আমিন হামজা|Md. Al Amin Hamja|নওগাঁ|Naogaon|EDAS|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-Common|Bed-09
30|Bukhari-1-30|Magura-68|মো. রিয়ান বিশ্বাস|Md. Rian Biswash|মাগুরা|Magura|WAMY-OSP|Bukhari-01|হাফেজ ওয়ালী উল্লাহ|P1-Down-Common|Bed-10
31|Bukhari-2-1|Bagura-21|মো. সিয়াম|Md. Siyam|বগুড়া|Bogura|WAMY-OSP|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-A|Bed-01
32|Bukhari-2-2|EDAS-1371|আবদুল আল মুজাহিদ|Abdul Al Muzahid|কুষ্টিয়া|Kustia|EDAS|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-A|Bed-02
33|Bukhari-2-3|Narail-51|রাহান সরদার|Rahan Sorder|নড়াইল|Narail|WAMY-OSP|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-A|Bed-03
34|Bukhari-2-4|EDAS-826|রিফাত ইসলাম|Rifat Islam|পঞ্চগড়|Panchagarh|EDAS|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-A|Bed-04
35|Bukhari-2-5|EDS-088|মুস্তাফিজুর রহমান|Mustafizur Rahman|রাজশাহী|Rajshahi|EDS|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-A|Bed-05
36|Bukhari-2-6|EDAS-1570|সাফায়েত উল্লাহ|Safayet Ullah|নরসিংদী|Narsingdi|EDAS|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-A|Bed-06
37|Bukhari-2-7|N/A|মো. সিরাতুল মোস্তাকিম|Md. Siratul Mostakim|বগুড়া|Bogura|WAMY-OSP|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-A|Bed-07
38|Bukhari-2-8|IAB05433|ফয়জুল ইসলাম|Foyjul Islam|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-A|Bed-08
39|Bukhari-2-9|BD-31231|মো. আলিফ|Md.Alif|গাজীপুর (IAB)|IAB-Gazipur|IAB|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-B|Bed-01
40|Bukhari-2-10|BD-31279|মোহাম্মদ আলী|Mohammed Ali|খুলনা (IAB)|IAB-Khulna|IAB|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-B|Bed-02
41|Bukhari-2-11|BD-31154|আরিফ হোসেন|Arif Hossen|সাতক্ষীরা (IAB)|IAB-Satkhira|IAB|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-B|Bed-03
42|Bukhari-2-12|BD-0105|তামিম হাসান নিশান|Tamim Hasan Nisan|যশোর|Jashore|WAMY-BD|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-B|Bed-04
43|Bukhari-2-13|22100201|কাউসার আহমদ রিফাত|Kawser Ahmad Refat|কুড়িগ্রাম|Kurigram|WAMY-OSP|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-B|Bed-05
44|Bukhari-2-14|EDAS-728|মো. আমিন ইসলাম|Md. Amin Islam|লালমনিরহাট|Lalmonirhat|EDAS|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-B|Bed-06
45|Bukhari-2-15|EDAS-1270|মো. জাকারিয়া ইসলাম|Md. Jakaria Islam|নওগাঁ|Naogaon|EDAS|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-B|Bed-07
46|Bukhari-2-16|EDAS-1582|সোয়াদ মিয়া|Soad Mia|নরসিংদী|Narsingdi|EDAS|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-B|Bed-08
47|Bukhari-2-17|EDS-133|ত্বকী তাহমিদ তাসকিন|Toky Tahmid Taskin|নাটোর|Natore|EDS|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-Common|Bed-01
48|Bukhari-2-18|N/A|আবদুল্লাহ|Abdullah|নীলফামারী|Nilphamari|WAMY-OSP|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-Common|Bed-02
49|Bukhari-2-19|EDAS-833|মো. জয়নাল আবেদীন|Md. Jaynal Abedin|পঞ্চগড়|Panchagarh|EDAS|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-Common|Bed-03
50|Bukhari-2-20|EDAS-1198|মো. সাইম|Md Saim|বরগুনা|Barguna|EDAS|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-Common|Bed-04
51|Bukhari-2-21|SKB 2834|মোহাম্মদ ইব্রাহিম|Mohammad Ibrahim|কক্সবাজার|Cox's Bazar|SKB|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-Common|Bed-05
52|Bukhari-2-22|02-23-057|মোহাম্মদ ওয়াজিবুল ইসলাম রামিম|Mohammad Wajibul Islam Ramim|চট্টগ্রাম (IAB)|IAB-Chattogram|IAB|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-Common|Bed-06
53|Bukhari-2-23|IAB0671|মনিরুল ইসলাম|Monirul Islam|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-Common|Bed-07
54|Bukhari-2-24|31494|মো. আরিফুর রহমান|Md. Arifur Rohman|লালমনিরহাট (IAB)|IAB-Lalmonirhat|IAB|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-Common|Bed-08
55|Bukhari-2-25|IAB0571|মো. আবদুল্লাহ সরকার|Md. Abdullah Sarker|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-Common|Bed-09
56|Bukhari-2-26|N/A|আপন|Apon|খুলনা|Khulna|WAMY-OSP|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-Common|Bed-10
57|Bukhari-2-27|EDAS-554|মো. শিমুল বাবু|Md. Shimul Babu|লালমনিরহাট|Lalmonirhat|EDAS|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-Common|Bed-11
58|Bukhari-2-28|EDAS 1617|আরাফাত হোসেন|Arafat Hosen|ময়মনসিংহ|Mymansing|EDAS|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-Common|Bed-12
59|Bukhari-2-29|Narail-43|মো. হোসেন|Md. Hosien|নড়াইল|Narail|WAMY-OSP|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-Common|Bed-13
60|Bukhari-2-30|EDAS-1551|মেহতাব হাসান|Mehtab Hasan|নরসিংদী|Narsingdi|EDAS|Bukhari-02|মো. শফিকুল ইসলাম|P1-Up-Common|Bed-14
61|Bukhari-3-1|Bagura-62|আবু হুজাইফা|Abu Huzaifa|বগুড়া|Bogura|WAMY-OSP|Bukhari-03|আক্তার হোসেন|P2-Down-A|Bed-01
62|Bukhari-3-2|EDAS-669|মো. রাসেল রানা|Md Rasel Rana|লালমনিরহাট|Lalmonirhat|EDAS|Bukhari-03|আক্তার হোসেন|P2-Down-A|Bed-02
63|Bukhari-3-3|EDAS-1524|মো. মোবাশ্বির আহমেদ|Md Mobassir Ahmed|নরসিংদী|Narsingdi|EDAS|Bukhari-03|আক্তার হোসেন|P2-Down-A|Bed-03
64|Bukhari-3-4|EDAS-829|মাশরাফি আহমেদ রাফি|Mashrafi Ahmed Rafi|পঞ্চগড়|Panchagarh|EDAS|Bukhari-03|আক্তার হোসেন|P2-Down-A|Bed-04
65|Bukhari-3-5|EDAS-973|মো. মেহেদী হাসান|Md. Mehdi Hasan|ঠাকুরগাঁও|Thakurgaon|EDAS|Bukhari-03|আক্তার হোসেন|P2-Down-A|Bed-05
66|Bukhari-3-6|EDAS-854|আরিফুল ইসলাম|Ariful Islam|পঞ্চগড়|Panchagarh|EDAS|Bukhari-03|আক্তার হোসেন|P2-Down-A|Bed-06
67|Bukhari-3-7|N/A|মো. সুলাইমান সাদিক|Md Sulaiman Sadik|চাঁপাইনবাবগঞ্জ|Chapainawabganj|WAMY-OSP|Bukhari-03|আক্তার হোসেন|P2-Down-A|Bed-07
68|Bukhari-3-8|IAB0661|ফাহিম মিয়া|Fahim Mia|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Bukhari-03|আক্তার হোসেন|P2-Down-B|Bed-01
69|Bukhari-3-9|BD-31639|আরিয়ান|Ariyan|খুলনা (IAB)|IAB-Khulna|IAB|Bukhari-03|আক্তার হোসেন|P2-Down-B|Bed-02
70|Bukhari-3-10|31730|মো. মাসুম বিল্লাহ|Md Masum Billah|কুড়িগ্রাম (IAB)|IAB-Kurigram|IAB|Bukhari-03|আক্তার হোসেন|P2-Down-B|Bed-03
71|Bukhari-3-11|BD-31696|সিয়াম হোসেন|Siyam Hossain|সাতক্ষীরা (IAB)|IAB-Satkhira|IAB|Bukhari-03|আক্তার হোসেন|P2-Down-B|Bed-04
72|Bukhari-3-12|BD-0188|মেহেদী হাসান আলিফ|Mehadi Hassan Alif|যশোর|Jashore|WAMY-BD|Bukhari-03|আক্তার হোসেন|P2-Down-B|Bed-05
73|Bukhari-3-13|EDAS-1301|মো. মুস্তাকিন ফারহান|Md. Mustakin Farhan|কুষ্টিয়া|Kustia|EDAS|Bukhari-03|আক্তার হোসেন|P2-Down-B|Bed-06
74|Bukhari-3-14|Magura-99|মো. আমির হামজা|Md. Amir Hamza|মাগুরা|Magura|WAMY-OSP|Bukhari-03|আক্তার হোসেন|P2-Down-B|Bed-07
75|Bukhari-3-15|Narail-45|মারজান|Marjan|নড়াইল|Narail|WAMY-OSP|Bukhari-03|আক্তার হোসেন|P2-Down-C|Bed-01
76|Bukhari-3-16|EDAS-1588|ইয়াসিন মিয়া|Yasin Mia|নরসিংদী|Narsingdi|EDAS|Bukhari-03|আক্তার হোসেন|P2-Down-C|Bed-02
77|Bukhari-3-17|EDS-207|মো. মুজাহিদ হোসেন|Md Mujahid Hosen|নীলফামারী|Nilphamari|EDS|Bukhari-03|আক্তার হোসেন|P2-Down-C|Bed-03
78|Bukhari-3-18|Pabna-22|আবরার ইসরাইল|Abrar Israel|পাবনা|Pabna|WAMY-OSP|Bukhari-03|আক্তার হোসেন|P2-Down-C|Bed-04
79|Bukhari-3-19|EDAS-794|কাউসার হোসেন|Kawsar Hossain|পঞ্চগড়|Panchagarh|EDAS|Bukhari-03|আক্তার হোসেন|P2-Down-C|Bed-05
80|Bukhari-3-20|Bagura-11|গোলাম মোস্তফা|Golam Mostafa|বগুড়া|Bogura|WAMY-OSP|Bukhari-03|আক্তার হোসেন|P2-Down-C|Bed-06
81|Bukhari-3-21|ICNA 1554|মোহাম্মদ ইমরান|Mohammad Imran|কক্সবাজার|Cox's Bazar|ICNA|Bukhari-03|আক্তার হোসেন|P2-Down-Common|Bed-01
82|Bukhari-3-22|SKB 2370|মোহাম্মদ সাখাওয়াত হোসেন জিসান|Mohammad Sakawat Hosen Jisan|কক্সবাজার|Cox's Bazar|SKB|Bukhari-03|আক্তার হোসেন|P2-Down-Common|Bed-02
83|Bukhari-3-23|BD-31246|হুজাইফা|Hujaifa|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Bukhari-03|আক্তার হোসেন|P2-Down-Common|Bed-03
84|Bukhari-3-24|IAB0138|জাহিদ হাসান সুমন|Zahid Hasan Sumon|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Bukhari-03|আক্তার হোসেন|P2-Down-Common|Bed-04
85|Bukhari-3-25|31496|মো. ইমরান আলী|Md. Imran Ali|লালমনিরহাট (IAB)|IAB-Lalmonirhat|IAB|Bukhari-03|আক্তার হোসেন|P2-Down-Common|Bed-05
86|Bukhari-3-26|IAB0607|মো. সাকিব আল হাসান|Md. Sakib Al Hashan|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Bukhari-03|আক্তার হোসেন|P2-Down-Common|Bed-06
87|Bukhari-3-27|N/A|ফেরদৌস|Ferdous|খুলনা|Khulna|WAMY-OSP|Bukhari-03|আক্তার হোসেন|P2-Down-Common|Bed-07
88|Bukhari-3-28|EDAS-677|মো. শামীম হোসেন|Md Shamim Hossen|লালমনিরহাট|Lalmonirhat|EDAS|Bukhari-03|আক্তার হোসেন|P2-Down-Common|Bed-08
89|Bukhari-3-29|EDAS 1625|মমিনুল ইসলাম|Mominul Islam|ময়মনসিংহ|Mymansing|EDAS|Bukhari-03|আক্তার হোসেন|P2-Down-Common|Bed-09
90|Bukhari-3-30|ICNA 72|আবদুল্লাহ সাদেক|Abdullah Sadek|কক্সবাজার|Cox's Bazar|ICNA|Bukhari-03|আক্তার হোসেন|P2-Down-Common|Bed-10
91|Bukhari-4-1|SKB 2509|মোহাম্মদ সাইফুল্লাহ|Mohammad Saifullah|জামালপুর|Jamalpur|SKB|Bukhari-04|আজিজুর রহমান|P2-Up-A|Bed-01
92|Bukhari-4-2|Magura-74|নূর মোহাম্মদ মিজি|Nur Mohammad Mizi|মাগুরা|Magura|WAMY-OSP|Bukhari-04|আজিজুর রহমান|P2-Up-A|Bed-02
93|Bukhari-4-3|EDAS-1540|হুজাইফা সরকার|Hujaifa Sarker|নরসিংদী|Narsingdi|EDAS|Bukhari-04|আজিজুর রহমান|P2-Up-A|Bed-03
94|Bukhari-4-4|EDAS-875|মো. মাহিম ইসলাম|Md. Mahim Islam|পঞ্চগড়|Panchagarh|EDAS|Bukhari-04|আজিজুর রহমান|P2-Up-A|Bed-04
95|Bukhari-4-5|SKB 2748|মীর আন্নাফিস|Mir Annafis|জামালপুর|Jamalpur|SKB|Bukhari-04|আজিজুর রহমান|P2-Up-A|Bed-05
96|Bukhari-4-6|N/A|ইভান|Evan|বরগুনা|Barguna|WAMY-OSP|Bukhari-04|আজিজুর রহমান|P2-Up-A|Bed-06
97|Bukhari-4-7|ICNA|জিহাদ হাসান রিয়াদ|Zihad Hasan Riyad|কক্সবাজার|Cox's Bazar|ICNA|Bukhari-04|আজিজুর রহমান|P2-Up-A|Bed-07
98|Bukhari-4-8|IAB0546|আরিফুল ইসলাম সাফায়েত|Ariful Islam Safayet|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Bukhari-04|আজিজুর রহমান|P2-Up-A|Bed-08
99|Bukhari-4-9|BD-31454|শরাফত হোসেন|Sharafit Hossan|খুলনা (IAB)|IAB-Khulna|IAB|Bukhari-04|আজিজুর রহমান|P2-Up-B|Bed-01
100|Bukhari-4-10|31731|মো. মুশফিকুর রহিম|Md Mushfikur Rahim|কুড়িগ্রাম (IAB)|IAB-Kurigram|IAB|Bukhari-04|আজিজুর রহমান|P2-Up-B|Bed-02
101|Bukhari-4-11|SKB 2321|জুনায়েদ হাসান জিহাদ|Junaid Hasan Jihad|জামালপুর|Jamalpur|SKB|Bukhari-04|আজিজুর রহমান|P2-Up-B|Bed-03
102|Bukhari-4-12|EDAS-1129|মো. রিপন মিয়া|Md. Ripon Mia|জয়পুরহাট|Joypurhat|EDAS|Bukhari-04|আজিজুর রহমান|P2-Up-B|Bed-04
103|Bukhari-4-13|EDAS-672|রোজিনা খাতুন|Rojina Khatun|লালমনিরহাট|Lalmonirhat|EDAS|Bukhari-04|আজিজুর রহমান|P2-Up-B|Bed-05
104|Bukhari-4-14|N/A|ইয়াসিন|Yasin|মাগুরা|Magura|WAMY-OSP|Bukhari-04|আজিজুর রহমান|P2-Up-B|Bed-06
105|Bukhari-4-15|EDAS-1554|জুনায়েদ মোল্লা|Zunayed Molla|নরসিংদী|Narsingdi|EDAS|Bukhari-04|আজিজুর রহমান|P2-Up-B|Bed-07
106|Bukhari-4-16|EDAS-1577|ইয়াসিন মিয়া|Yasin Mia|নরসিংদী|Narsingdi|EDAS|Bukhari-04|আজিজুর রহমান|P2-Up-B|Bed-08
107|Bukhari-4-17|EDS-235|মো. নাজমুল হক|Md. Nazmul Haque|নীলফামারী|Nilphamari|EDS|Bukhari-04|আজিজুর রহমান|P2-Up-Common|Bed-01
108|Bukhari-4-18|EDAS-795|শরীফ হোসেন|Shorif Hossain|পঞ্চগড়|Panchagarh|EDAS|Bukhari-04|আজিজুর রহমান|P2-Up-Common|Bed-02
109|Bukhari-4-19|EDAS-77|মোহাম্মদ মনফায়াজ আলিফ|Mohammad Monfayaj Alif|সাতক্ষীরা|Satkhira|EDAS|Bukhari-04|আজিজুর রহমান|P2-Up-Common|Bed-03
110|Bukhari-4-20|N/A|মো. হাসিব|Md Hasib|চাঁপাইনবাবগঞ্জ|Chapainawabganj|WAMY-OSP|Bukhari-04|আজিজুর রহমান|P2-Up-Common|Bed-04
111|Bukhari-4-21|ICNA|মোহাম্মদ মুসা|Mohammad Musa|কক্সবাজার|Cox's Bazar|ICNA|Bukhari-04|আজিজুর রহমান|P2-Up-Common|Bed-05
112|Bukhari-4-22|SKB 1711|মোহাম্মদ জেহান|Mohammad Jehan|কক্সবাজার|Cox's Bazar|SKB|Bukhari-04|আজিজুর রহমান|P2-Up-Common|Bed-06
113|Bukhari-4-23|BD-31462|মো. মাহবুব|Md.Mahabub|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Bukhari-04|আজিজুর রহমান|P2-Up-Common|Bed-07
114|Bukhari-4-24|BD-31503|মো. তামিম আহমেদ|Md. Tamim Ahmed|গাজীপুর (IAB)|IAB-Gazipur|IAB|Bukhari-04|আজিজুর রহমান|P2-Up-Common|Bed-08
115|Bukhari-4-25|410|আবদুল্লাহ চৌকিদার|Abdullah Chukidar|মুন্সীগঞ্জ (IAB)|IAB-Munshigonj|IAB|Bukhari-04|আজিজুর রহমান|P2-Up-Common|Bed-09
116|Bukhari-4-26|SKB2814|জুবাইল ইসলাম|Jubail Islam|জামালপুর|Jamalpur|SKB|Bukhari-04|আজিজুর রহমান|P2-Up-Common|Bed-10
117|Bukhari-4-27|22100177|মুরসালিন|Morsalin|কুড়িগ্রাম|Kurigram|WAMY-OSP|Bukhari-04|আজিজুর রহমান|P2-Up-Common|Bed-11
118|Bukhari-4-28|Meherpur-12|জুনায়েদ খান|Junayed Khan|মেহেরপুর|Meherpur|WAMY-OSP|Bukhari-04|আজিজুর রহমান|P2-Up-Common|Bed-12
119|Bukhari-4-29|EDAS-1273|মো. সজীব ইসলাম|Md. Shojib Islam|নওগাঁ|Naogaon|EDAS|Bukhari-04|আজিজুর রহমান|P2-Up-Common|Bed-13
120|Bukhari-4-30|BD-0089|মিকদাদ খান সাজিম|Megdad Khan Sajim|যশোর|Jashore|WAMY-BD|Bukhari-04|আজিজুর রহমান|P2-Up-Common|Bed-14
    """

    private const val SHAFEYI_TSV = """
121|Shafi'ee-1-1|Narail-57|রাকিবুল মোল্লা|Rakibul Molla|নড়াইল|Narail|WAMY-OSP|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-A|Bed-01
122|Shafi'ee-1-2|EDS-070|নাহিদ হুসেন|Nahid Husen|রাজশাহী|Rajshahi|EDS|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-A|Bed-02
123|Shafi'ee-1-3|Bagura-90|মইনুল ইসলাম|Moinul Islam|বগুড়া|Bogura|WAMY-OSP|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-A|Bed-03
124|Shafi'ee-1-4|BD-31504|মো. জুনায়েদ আহমেদ|Md. Junaed Ahmed|গাজীপুর (IAB)|IAB-Gazipur|IAB|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-A|Bed-04
125|Shafi'ee-1-5|SKB 2744|আবদুর রহমান|Abdur Rahman|জামালপুর|Jamalpur|SKB|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-A|Bed-05
126|Shafi'ee-1-6|EDAS 1622|মুস্তাকিম হোসেন|Mustakim Hosen|ময়মনসিংহ|Mymansing|EDAS|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-A|Bed-06
127|Shafi'ee-1-7|Rajshahi-296|রিয়াদ|Riad|রাজশাহী|Rajshahi|WAMY-OSP|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-A|Bed-07
128|Shafi'ee-1-8|ICNA|তৌহিদুল আলম|Towhidul Alom|কক্সবাজার|Cox's Bazar|ICNA|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-B|Bed-01
129|Shafi'ee-1-9|31175|মো. সাকিব আল হাসান|Md Sakibal Hasan|কুড়িগ্রাম (IAB)|IAB-Kurigram|IAB|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-B|Bed-02
130|Shafi'ee-1-10|IAB0606|মো. সাব্বির হাসান|Md. Sabbir Hasan|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-B|Bed-03
131|Shafi'ee-1-11|EDAS-1207|মো. শরিফুল ইসলাম|Md. Shariful Islam|নওগাঁ|Naogaon|EDAS|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-B|Bed-04
132|Shafi'ee-1-12|ICNA 2|সারোয়ার হান্নান আদি|Sarwar Hannan Adi|কক্সবাজার|Cox's Bazar|ICNA|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-B|Bed-05
133|Shafi'ee-1-13|22100195|শামীম আহমেদ|Shamim Ahmed|কুড়িগ্রাম|Kurigram|WAMY-OSP|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-B|Bed-06
134|Shafi'ee-1-14|Meherpur-13|সাদিকুর রহমান|Shadiqur Rohman|মেহেরপুর|Meherpur|WAMY-OSP|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-B|Bed-07
135|Shafi'ee-1-15|EDS-261|মো. মোস্তফা হাবিব|Md. Mostafa Habib|রাজশাহী|Rajshahi|EDS|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-C|Bed-01
136|Shafi'ee-1-16|IAB0683|আবু হানিফ|Abu Hanif|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-C|Bed-02
137|Shafi'ee-1-17|BD-31631|আবির মোল্লা|Abir Molla|খুলনা (IAB)|IAB-Khulna|IAB|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-C|Bed-03
138|Shafi'ee-1-18|EDAS- 1719|সাফায়েত|Safayet|সাতক্ষীরা|Satkhira|EDAS|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-C|Bed-04
139|Shafi'ee-1-19|BD-2844|সাফায়েত হোসেন সৈকত|Safayet Hossen Saykat|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-C|Bed-05
140|Shafi'ee-1-20|BD-31236|সাকিব আল হাসান|Sakib Al Hasan|গাজীপুর (IAB)|IAB-Gazipur|IAB|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-C|Bed-06
141|Shafi'ee-1-21|22100198|আবদুল কাইয়ুম|Abdul Qayum|কুড়িগ্রাম|Kurigram|WAMY-OSP|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-Common|Bed-01
142|Shafi'ee-1-22|EDAS-582|লিমন বাবু|Limon Babu|লালমনিরহাট|Lalmonirhat|EDAS|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-Common|Bed-02
143|Shafi'ee-1-23|EDAS 1632|আবতাহী আহনাফ|Abtahi Ahnaf|ময়মনসিংহ|Mymansing|EDAS|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-Common|Bed-03
144|Shafi'ee-1-24|EDAS-564|রমজান খান|Ramjan Khan|পটুয়াখালী|Patuakhali|EDAS|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-Common|Bed-04
145|Shafi'ee-1-25|BD-31097|আবদুল্লাহ আল নোমান|Abdullah Al Noman|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-Common|Bed-05
146|Shafi'ee-1-26|IAB0534|মো. আল আমিন|Md. Al-Amin|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-Common|Bed-06
147|Shafi'ee-1-27|BD-30934|মো. ইমন হোসেন|Md. Emon Hossen|খুলনা (IAB)|IAB-Khulna|IAB|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-Common|Bed-07
148|Shafi'ee-1-28|EDS-155|তৌফিক আলী|Toufiq Ali|ঝিনাইদহ|Jhenaidah|EDS|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-Common|Bed-08
149|Shafi'ee-1-29|BD-31099|মো. আবদুর রহমান রিপন|Md. Abdur Rahman Ripon|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-Common|Bed-09
150|Shafi'ee-1-30|BD-31288|সাগর শেখ|Sagor Shekh|খুলনা (IAB)|IAB-Khulna|IAB|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-Common|Bed-10
151|Shafi'ee-1-31|EDS-066|সাদাকুল ইসলাম|Sadakul Islam|রাজশাহী|Rajshahi|EDS|Shafi'ee-01|হাফেজ জাকারিয়া হোসাইন|P3-Down-Common|Bed-11
152|Shafi'ee-2-1|EDAS-806|সোহান ইসলাম|Shohan Islam|পঞ্চগড়|Panchagarh|EDAS|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-A|Bed-01
153|Shafi'ee-2-2|N/A|জুনায়েদ|Zunayed|রাজশাহী|Rajshahi|WAMY-OSP|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-A|Bed-02
154|Shafi'ee-2-3|EDS-435|মো. লাবিন আলী|Md. Labin Ali|বগুড়া|Bogura|EDS|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-A|Bed-03
155|Shafi'ee-2-4|31732|মো. সালমান ফারসি|Md Salman Farshi|কুড়িগ্রাম (IAB)|IAB-Kurigram|IAB|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-A|Bed-04
156|Shafi'ee-2-5|EDAS-1155|মো. সৌরভ ইসলাম|Md. Showrav Islam|জয়পুরহাট|Joypurhat|EDAS|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-A|Bed-05
157|Shafi'ee-2-6|EDAS-1290|মো. ওমর ফারুক|Md. Omar Faruq|নওগাঁ|Naogaon|EDAS|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-A|Bed-06
158|Shafi'ee-2-7|EDS-067|আতিক রহমান আশিক|Atiq Rahman Ashiq|রাজশাহী|Rajshahi|EDS|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-A|Bed-07
159|Shafi'ee-2-8|ICNA 116|মো. আবদুর রহমান|Md. Abdur Rahman|কক্সবাজার|Cox's Bazar|ICNA|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-A|Bed-08
160|Shafi'ee-2-9|31525|শান্ত হোসেন রাব্বি|Shanto Hossen Rabby|রাজবাড়ী (IAB)|IAB-Rajbari|IAB|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-B|Bed-01
161|Shafi'ee-2-10|EDAS-1232|মো. সিয়াম|Md. Siam|নওগাঁ|Naogaon|EDAS|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-B|Bed-02
162|Shafi'ee-2-11|ICNA 133|আবরারুল হক|Abrarul Hoque|কক্সবাজার|Cox's Bazar|ICNA|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-B|Bed-03
163|Shafi'ee-2-12|31493|মো. আবদুল কাইয়ুম|Md. Abdul Kaiyum|লালমনিরহাট (IAB)|IAB-Lalmonirhat|IAB|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-B|Bed-04
164|Shafi'ee-2-13|22100170|এনামুল হক|Enamul Haque|কুড়িগ্রাম|Kurigram|WAMY-OSP|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-B|Bed-05
165|Shafi'ee-2-14|N/A|সায়েম|Sayem|নীলফামারী|Nilphamari|WAMY-OSP|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-B|Bed-06
166|Shafi'ee-2-15|EDAS-411|মো. সালমান ফারসি|Md Salman Farsi|বরগুনা|Barguna|EDAS|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-B|Bed-07
167|Shafi'ee-2-16|IAB0665|তামিম ওসমান|Tamim Osman|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-B|Bed-08
168|Shafi'ee-2-17|408|আবদুল্লাহ মিয়া|Abdullah Mia|মুন্সীগঞ্জ (IAB)|IAB-Munshigonj|IAB|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-B|Bed-09
169|Shafi'ee-2-18|EDS-394|মো. তারিকুল ইসলাম|Md. Tariqul Islam|সিরাজগঞ্জ|Sirajganj|EDS|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-Common|Bed-01
170|Shafi'ee-2-19|BD-31119|মো. আবদুল মোতালেব|Md. Abdul Motaleb|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-Common|Bed-02
171|Shafi'ee-2-20|BD-31285|মো. সাকিবুল ইসলাম|Md.Sakibul Islam|খুলনা (IAB)|IAB-Khulna|IAB|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-Common|Bed-03
172|Shafi'ee-2-21|22100207|রাফিউল্লাহ রাব্বি|Rafiullah Rabbi|কুড়িগ্রাম|Kurigram|WAMY-OSP|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-Common|Bed-04
173|Shafi'ee-2-22|Magura-35|তানভীর বিশ্বাস|Tanvir Biswash|মাগুরা|Magura|WAMY-OSP|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-Common|Bed-05
174|Shafi'ee-2-23|EDS-127|আমির খসরু|Amir Khosru|নাটোর|Natore|EDS|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-Common|Bed-06
175|Shafi'ee-2-24|Bagura-54|আলী ইয়াসিন|Ali Yasin|বগুড়া|Bogura|WAMY-OSP|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-Common|Bed-07
176|Shafi'ee-2-25|BD-31469|আবু আনাস সিফাত|Abu Anas Sifat|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-Common|Bed-08
177|Shafi'ee-2-26|BD-31643|ইব্রাহিম|Ibrahim|খুলনা (IAB)|IAB-Khulna|IAB|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-Common|Bed-09
178|Shafi'ee-2-27|BD-31449|মো. ইসমাইল|Md. Ismail|খুলনা (IAB)|IAB-Khulna|IAB|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-Common|Bed-10
179|Shafi'ee-2-28|EDAS-718|মুস্তাকিম ইসলাম|Mustakim Islam|লালমনিরহাট|Lalmonirhat|EDAS|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-Common|Bed-11
180|Shafi'ee-2-29|BD-31502|মো. আবদুল্লাহ হাসান|Md. Abdullah Hasan|গাজীপুর (IAB)|IAB-Gazipur|IAB|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-Common|Bed-12
181|Shafi'ee-2-30|02-23-061|কাজী মোহাম্মদ তাহমিদ আবিদ|Kazi Mohammad Tahmid Abid|চট্টগ্রাম (IAB)|IAB-Chattogram|IAB|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-Common|Bed-13
182|Shafi'ee-2-31|EDAS- 1722|সিয়াম হোসেন রনি|Siam Hosen Roni|সাতক্ষীরা|Satkhira|EDAS|Shafi'ee-02|আরমান হোসাইন (APP)|P3-Up-Common|Bed-14
183|Shafi'ee-3-1|EDAS-1733|আবু জিহাদ|Abu Jihad|পটুয়াখালী|Patuakhali|EDAS|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-A|Bed-01
184|Shafi'ee-3-2|N/A|আতিকুল ইসলাম|Atiqul Islam|রাজশাহী|Rajshahi|WAMY-OSP|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-A|Bed-02
185|Shafi'ee-3-3|22100147|শ্রাবণ মিয়া|Shrabon Mia|গাইবান্ধা|Gaibandha|WAMY-OSP|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-A|Bed-03
186|Shafi'ee-3-4|498|মো. আবদুর রহিম|Md. Abdur Rahim|মুন্সীগঞ্জ (IAB)|IAB-Munshigonj|IAB|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-A|Bed-04
187|Shafi'ee-3-5|EDAS-685|মো. স্বাধীন হোসেন|Md Sadhin Hosen|লালমনিরহাট|Lalmonirhat|EDAS|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-A|Bed-05
188|Shafi'ee-3-6|EDS-119|আতিকুল ইসলাম তামিম|Atiqul Islam Tamim|নাটোর|Natore|EDS|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-A|Bed-06
189|Shafi'ee-3-7|N/A|সামিউল ইসলাম|Samiul Islam|বগুড়া|Bogura|WAMY-OSP|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-A|Bed-07
190|Shafi'ee-3-8|30930|মো. রবিউল আলম|Md Robiul Alom|চাঁদপুর (IAB)|IAB-Chandpur|IAB|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-B|Bed-01
191|Shafi'ee-3-9|31437|মো. আবদুল্লাহ|Md Abdullah|রাজবাড়ী (IAB)|IAB-Rajbari|IAB|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-B|Bed-02
192|Shafi'ee-3-10|Narail-44|মো. রাফিজ|Md. Rafij|নড়াইল|Narail|WAMY-OSP|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-B|Bed-03
193|Shafi'ee-3-11|3761|মো. হুমায়ুন শেখ|Md Humayon Sheikh|রাজবাড়ী (IAB)|IAB-Rajbari|IAB|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-B|Bed-04
194|Shafi'ee-3-12|SKB 2615|আবির হোসেন শুভ|Abir Hosen Shuvo|জামালপুর|Jamalpur|SKB|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-B|Bed-05
195|Shafi'ee-3-13|EDAS-726|মিনহাজ শাহরিয়ার|Minhaz Sahariyer|লালমনিরহাট|Lalmonirhat|EDAS|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-B|Bed-06
196|Shafi'ee-3-14|Pabna-61|সিজান|Sizan|পাবনা|Pabna|WAMY-OSP|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-B|Bed-07
197|Shafi'ee-3-15|Bagura-23|আবু হোসেন|Abu Hossain|বগুড়া|Bogura|WAMY-OSP|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-C|Bed-01
198|Shafi'ee-3-16|BD-30916|মো. তানজিম খান|Md. Tanjim Khan|গাজীপুর (IAB)|IAB-Gazipur|IAB|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-C|Bed-02
199|Shafi'ee-3-17|EDAS 1637|আতিকুল ইসলাম|Atikul Islam|ময়মনসিংহ|Mymansing|EDAS|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-C|Bed-03
200|Shafi'ee-3-18|SKB 2722|মোহাম্মদ সোহেল|Mohammad Sohel|কক্সবাজার|Cox's Bazar|SKB|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-C|Bed-04
201|Shafi'ee-3-19|02-23-060|মো. তামিম|Md.Tamim|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-C|Bed-05
202|Shafi'ee-3-20|BD-30942|মো. হাশেম আলী হাওলাদার|Md. Hashem Ali Hawladar|খুলনা (IAB)|IAB-Khulna|IAB|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-C|Bed-06
203|Shafi'ee-3-21|22100166|জিহাদ আলী|Jehad Ali|কুড়িগ্রাম|Kurigram|WAMY-OSP|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-Common|Bed-01
204|Shafi'ee-3-22|Meherpur-14|মো. সাইফ|Md. Saif|মেহেরপুর|Meherpur|WAMY-OSP|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-Common|Bed-02
205|Shafi'ee-3-23|Pabna-48|সাফফাত রহিম হামিম|Saffat Rahimu Hamim|পাবনা|Pabna|WAMY-OSP|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-Common|Bed-03
206|Shafi'ee-3-24|SKB 2358|মোহাম্মদ আরিফ|Mohammad Arif|কক্সবাজার|Cox's Bazar|SKB|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-Common|Bed-04
207|Shafi'ee-3-25|BD-31704|মো. মাহফুজ|Md. Mahfuj|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-Common|Bed-05
208|Shafi'ee-3-26|BD-31449|ইসমাইল হিসান|Esmil Hissan|খুলনা (IAB)|IAB-Khulna|IAB|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-Common|Bed-06
209|Shafi'ee-3-27|31176|মো. সিফাত আহমেদ|Md Sifat Ahmed|কুড়িগ্রাম (IAB)|IAB-Kurigram|IAB|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-Common|Bed-07
210|Shafi'ee-3-28|ICNA 5|সাকিমুল হাসান মুবিন|Sakimul Hasan Mubin|কক্সবাজার|Cox's Bazar|ICNA|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-Common|Bed-08
211|Shafi'ee-3-29|Magura-93|মো. মুসাদুল ইসলাম|Md. Musadul Islam|মাগুরা|Magura|WAMY-OSP|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-Common|Bed-09
212|Shafi'ee-3-30|EDAS-1572|এহসানুল হক ইয়ামিন|Ehsanul Haque Yeamin|নরসিংদী|Narsingdi|EDAS|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-Common|Bed-10
213|Shafi'ee-3-31|02-23-006|মো. সাদেক হোসেন সওবিন|Md Sadek Hossen Sowbin|চট্টগ্রাম (IAB)|IAB-Chattogram|IAB|Shafi'ee-03|ইমরান রহমান (WDH)|P4-Down-Common|Bed-11
214|Shafi'ee-4-1|EDS-068|ইব্রাহিম|Ibrahim|রাজশাহী|Rajshahi|EDS|Shafi'ee-04|জিয়াউল হক|P4-Up-A|Bed-01
215|Shafi'ee-4-2|N/A|আল-আমিন|Alamin|রাজশাহী|Rajshahi|WAMY-OSP|Shafi'ee-04|জিয়াউল হক|P4-Up-A|Bed-02
216|Shafi'ee-4-3|BD-31116|মো. শাকিল|Md. Shakil|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Shafi'ee-04|জিয়াউল হক|P4-Up-A|Bed-03
217|Shafi'ee-4-4|489|আল আমিন হোসেন|Al Amin Hossain|মুন্সীগঞ্জ (IAB)|IAB-Munshigonj|IAB|Shafi'ee-04|জিয়াউল হক|P4-Up-A|Bed-04
218|Shafi'ee-4-5|Magura-45|সোনা মিয়া ইয়াদিন|Sona Mia Yadin|মাগুরা|Magura|WAMY-OSP|Shafi'ee-04|জিয়াউল হক|P4-Up-A|Bed-05
219|Shafi'ee-4-6|EDS-058|মোহাম্মদ আবদুল ওয়াহিদ|Mohammad Abdul Wahid|রাজশাহী|Rajshahi|EDS|Shafi'ee-04|জিয়াউল হক|P4-Up-A|Bed-06
220|Shafi'ee-4-7|ICNA 125|মোহাম্মদ সোহেল|Mohammad Sohel|কক্সবাজার|Cox's Bazar|ICNA|Shafi'ee-04|জিয়াউল হক|P4-Up-A|Bed-07
221|Shafi'ee-4-8|IAB0664|রিয়াদ হাসান|Riad Hasan|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Shafi'ee-04|জিয়াউল হক|P4-Up-A|Bed-08
222|Shafi'ee-4-9|31439|মো. রহিম শেখ|Md Rahim Sheikh|রাজবাড়ী (IAB)|IAB-Rajbari|IAB|Shafi'ee-04|জিয়াউল হক|P4-Up-B|Bed-01
223|Shafi'ee-4-10|N/A|মো. নাহিদ বাবু|Md Nahid Babu|বগুড়া|Bogura|WAMY-OSP|Shafi'ee-04|জিয়াউল হক|P4-Up-B|Bed-02
224|Shafi'ee-4-11|Bagura-84|মেহেদী হাসান|Mehedi Hasan|বগুড়া|Bogura|WAMY-OSP|Shafi'ee-04|জিয়াউল হক|P4-Up-B|Bed-03
225|Shafi'ee-4-12|N/A|ওমর ফারুক|Omar Faruk|জয়পুরহাট|Joypurhat|WAMY-OSP|Shafi'ee-04|জিয়াউল হক|P4-Up-B|Bed-04
226|Shafi'ee-4-13|EDAS-751|আল আমিন|Al-Amin|লালমনিরহাট|Lalmonirhat|EDAS|Shafi'ee-04|জিয়াউল হক|P4-Up-B|Bed-05
227|Shafi'ee-4-14|EDAS-239|তানভীর হোসেন|Tanvir Hossen|পটুয়াখালী|Patuakhali|EDAS|Shafi'ee-04|জিয়াউল হক|P4-Up-B|Bed-06
228|Shafi'ee-4-15|ICNA|মো. হাসানুল ইসলাম|Md. Hasanul Islam|কক্সবাজার|Cox's Bazar|ICNA|Shafi'ee-04|জিয়াউল হক|P4-Up-B|Bed-07
229|Shafi'ee-4-16|BD-31642|মো. শরিফুল ইসলাম ফাহিম|Md.Soriful Islam Fahim|খুলনা (IAB)|IAB-Khulna|IAB|Shafi'ee-04|জিয়াউল হক|P4-Up-B|Bed-08
230|Shafi'ee-4-17|EDS-086|সুরাইয়া ফেরদৌস তাসিন|Suraya Ferdows Tasin|রাজশাহী|Rajshahi|EDS|Shafi'ee-04|জিয়াউল হক|P4-Up-Common|Bed-01
231|Shafi'ee-4-18|22100112|আল আমিন|Al- Amin|গাইবান্ধা|Gaibandha|WAMY-OSP|Shafi'ee-04|জিয়াউল হক|P4-Up-Common|Bed-02
232|Shafi'ee-4-19|IAB0686|সালমান গণি|Salman Goni|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Shafi'ee-04|জিয়াউল হক|P4-Up-Common|Bed-03
233|Shafi'ee-4-20|BD-0017|মোহাম্মদ তাজমিন|Mohammad Tajmin|যশোর|Jashore|WAMY-BD|Shafi'ee-04|জিয়াউল হক|P4-Up-Common|Bed-04
234|Shafi'ee-4-21|EDAS-1374|মো. তামজিদ হাসান মাহি|Md. Tamzid Hasan Mahi|কুষ্টিয়া|Kustia|EDAS|Shafi'ee-04|জিয়াউল হক|P4-Up-Common|Bed-05
235|Shafi'ee-4-22|EDAS 1623|ফরিদুল ইসলাম নাঈম|Faridul Islam Nayem|ময়মনসিংহ|Mymansing|EDAS|Shafi'ee-04|জিয়াউল হক|P4-Up-Common|Bed-06
236|Shafi'ee-4-23|EDAS-838|আরমান হোসেন|Arman Hossain|পঞ্চগড়|Panchagarh|EDAS|Shafi'ee-04|জিয়াউল হক|P4-Up-Common|Bed-07
237|Shafi'ee-4-24|SKB 2272|মোহাম্মদ মুরশেদ|Mohammad Murshed|কক্সবাজার|Cox's Bazar|SKB|Shafi'ee-04|জিয়াউল হক|P4-Up-Common|Bed-08
238|Shafi'ee-4-25|IAB0529|সোহান ইব্রাহিম|Shohan Ibrahim|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Shafi'ee-04|জিয়াউল হক|P4-Up-Common|Bed-09
239|Shafi'ee-4-26|BD-31289|মারুফ খান|Maruf Khan|খুলনা (IAB)|IAB-Khulna|IAB|Shafi'ee-04|জিয়াউল হক|P4-Up-Common|Bed-10
240|Shafi'ee-4-27|IAB0614|মো. নাঈম|Md. Naeem|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Shafi'ee-04|জিয়াউল হক|P4-Up-Common|Bed-11
241|Shafi'ee-4-28|SKB 2390|মোহাম্মদ সিফাত|Mohammad Sifad|কক্সবাজার|Cox's Bazar|SKB|Shafi'ee-04|জিয়াউল হক|P4-Up-Common|Bed-12
242|Shafi'ee-4-29|BD-31633|মো. আসমতুল্লাহ|Md Asmatullah|খুলনা (IAB)|IAB-Khulna|IAB|Shafi'ee-04|জিয়াউল হক|P4-Up-Common|Bed-13
243|Shafi'ee-4-30|EDS-455|তামজিদ মণ্ডল|Tamjid Mondol|পাবনা|Pabna|EDS|Shafi'ee-04|জিয়াউল হক|P4-Up-Common|Bed-14
    """

    private const val SALAHUDDIN_TSV = """
244|Ayubi-1-1|IAB0697|সজীব|Sajib|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-A|Bed-01
245|Ayubi-1-2|BD-30957|মো. আবদুর রহমান|Md. Abdur Rahman|গাজীপুর (IAB)|IAB-Gazipur|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-A|Bed-02
246|Ayubi-1-3|EDS-399|মো. মাশিয়াত মাহিম|Md. Mashiat Mahim|সিরাজগঞ্জ|Sirajganj|EDS|Ayubi-01|আলী আকবর (IAB)|P5-Down-A|Bed-03
247|Ayubi-1-4|31271|মো. আবদুল্লাহ প্রধান|Md Abdulla Prodan|চাঁদপুর (IAB)|IAB-Chandpur|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-A|Bed-04
248|Ayubi-1-5|BD-31030|মো. ইয়াসিন|Nd. Yasin|গাজীপুর (IAB)|IAB-Gazipur|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-A|Bed-05
249|Ayubi-1-6|31011|মো. ইসরাফিল ইসলাম|Md. Israfil Islam|লালমনিরহাট (IAB)|IAB-Lalmonirhat|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-A|Bed-06
250|Ayubi-1-7|IAB0609|মো. সীমান্ত|Md. Simanto|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-A|Bed-07
251|Ayubi-1-8|EDAS-213|মো. দীন ইসলাম|Md Din Islam|পটুয়াখালী|Patuakhali|EDAS|Ayubi-01|আলী আকবর (IAB)|P5-Down-B|Bed-01
252|Ayubi-1-9|BD-31243|মো. আল আমিন|Md.Al Amin|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-B|Bed-02
253|Ayubi-1-10|IAB0519|আহাদ বাবু|Ahad Babu|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-B|Bed-03
254|Ayubi-1-11|BD-30944|মো. শরিফুল ইসলাম|Md. Shariful Islam|খুলনা (IAB)|IAB-Khulna|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-B|Bed-04
255|Ayubi-1-12|31489|মো. হোসেন|Md Hossain|কুড়িগ্রাম (IAB)|IAB-Kurigram|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-B|Bed-05
256|Ayubi-1-13|31188|মো. তামিম হোসেন|Md Tamim Hossain|রাজবাড়ী (IAB)|IAB-Rajbari|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-B|Bed-06
257|Ayubi-1-14|IAB0658|এস. এম. সাজিদ মাহমুদ|S.M. Sajid Mahmud|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-B|Bed-07
258|Ayubi-1-15|SKB 1679|মোহাম্মদ ইসমাইল খোকা|Mohammad Ismail Khoka|কক্সবাজার|Cox's Bazar|SKB|Ayubi-01|আলী আকবর (IAB)|P5-Down-C|Bed-01
259|Ayubi-1-16|BD-3713|আরাফাত সাকিব চৌধুরী|Arafat Sakib Chowdhory|খুলনা (IAB)|IAB-Khulna|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-C|Bed-02
260|Ayubi-1-17|IAB0666|সালমান ফারসি|Salman Farshi|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-C|Bed-03
261|Ayubi-1-18|N/A|আল আমিন|Al Amin|কিশোরগঞ্জ|Kishorganj|WAMY-OSP|Ayubi-01|আলী আকবর (IAB)|P5-Down-C|Bed-04
262|Ayubi-1-19|BD-31621|মিরাজুল ইসলাম|Mirajul Islam|খুলনা (IAB)|IAB-Khulna|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-C|Bed-05
263|Ayubi-1-20|22100186|আরাফাত রহমান|Arafat Rahman|কুড়িগ্রাম|Kurigram|WAMY-OSP|Ayubi-01|আলী আকবর (IAB)|P5-Down-C|Bed-06
264|Ayubi-1-21|ICNA 122|মোবারক হোসেন|Mobarak Hosen|কক্সবাজার|Cox's Bazar|ICNA|Ayubi-01|আলী আকবর (IAB)|P5-Down-Common|Bed-01
265|Ayubi-1-22|IAB0469|সিয়াম মাহমুদা রাফিয়া|Siam Mahamuda Rafia|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-Common|Bed-02
266|Ayubi-1-23|IAB0551|রাকিব হাসান|Rakib Hasan|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-Common|Bed-03
267|Ayubi-1-24|EDAS-1305|মো. মুশফিকুর রহিম আবির|Md. Musfikur Rahim Abir|কুষ্টিয়া|Kustia|EDAS|Ayubi-01|আলী আকবর (IAB)|P5-Down-Common|Bed-04
268|Ayubi-1-25|EDAS-198|মো. জিহাদ হোসেন|Md Zihad Hossen|পটুয়াখালী|Patuakhali|EDAS|Ayubi-01|আলী আকবর (IAB)|P5-Down-Common|Bed-05
269|Ayubi-1-26|IAB0214|রেজওয়ান সরকার|Rejwan Shorkar|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-Common|Bed-06
270|Ayubi-1-27|EDAS-1306|মো. সিরাজ আলী|Md. Siraj Ali|কুষ্টিয়া|Kustia|EDAS|Ayubi-01|আলী আকবর (IAB)|P5-Down-Common|Bed-07
271|Ayubi-1-28|BD-31615|মো. ইমন হোসেন|Md.Emon Hossen|খুলনা (IAB)|IAB-Khulna|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-Common|Bed-08
272|Ayubi-1-29|BD-3497|মো. ইমরান হোসেন|Md.Imran Hossen|সাতক্ষীরা (IAB)|IAB-Satkhira|IAB|Ayubi-01|আলী আকবর (IAB)|P5-Down-Common|Bed-09
273|Ayubi-1-30|Magura-79|মো. আবু তালহা ইসলাম|Md. Abu Talha Islam|মাগুরা|Magura|WAMY-OSP|Ayubi-01|আলী আকবর (IAB)|P5-Down-Common|Bed-10
274|Ayubi-1-31|Narail-86|সিয়াম|Shiyam|নড়াইল|Narail|WAMY-OSP|Ayubi-01|আলী আকবর (IAB)|P5-Down-Common|Bed-11
275|Ayubi-2-1|EDAS-1571|রুবায়েত হোসেন রাহাত|Rubayet Hossen Rahat|নরসিংদী|Narsingdi|EDAS|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-A|Bed-01
276|Ayubi-2-2|31007|মো. আবদুল লতিফ|Md. Abdul Latif|লালমনিরহাট (IAB)|IAB-Lalmonirhat|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-A|Bed-02
277|Ayubi-2-3|ICNA 2261|ইমরান হোসেন তওসিফ|Imran Hosen Towsif|কক্সবাজার|Cox's Bazar|ICNA|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-A|Bed-03
278|Ayubi-2-4|IAB0687|রাকিবুল হাসান|Rakibul Hasan|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-A|Bed-04
279|Ayubi-2-5|BD-31623|মো. রায়হান হোসেন|Md Rayhan Hossain|খুলনা (IAB)|IAB-Khulna|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-A|Bed-05
280|Ayubi-2-6|405|নাজমুল ইসলাম|Nazmul Islam|মুন্সীগঞ্জ (IAB)|IAB-Munshigonj|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-A|Bed-06
281|Ayubi-2-7|N/A|শাহরিয়ান হক|Shahrian Haque|কিশোরগঞ্জ|Kishorganj|WAMY-OSP|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-A|Bed-07
282|Ayubi-2-8|SKB 1707|মোহাম্মদ ফারহান|Mohammad Farhan|কক্সবাজার|Cox's Bazar|SKB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-A|Bed-08
283|Ayubi-2-9|BD-31461|মো. আবদুল কাইয়ুম|Md.Abdul Kayum|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-B|Bed-01
284|Ayubi-2-10|BD-30960|মো. রুহান হাসান রামিম|Md. Ruhan Hasan Ramim|গাজীপুর (IAB)|IAB-Gazipur|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-B|Bed-02
285|Ayubi-2-11|31170|মো. বিপ্লব মিয়া|Md Biplob Mia|কুড়িগ্রাম (IAB)|IAB-Kurigram|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-B|Bed-03
286|Ayubi-2-12|31497|মো. মেরাজ আলী|Md. Meraj Ali|লালমনিরহাট (IAB)|IAB-Lalmonirhat|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-B|Bed-04
287|Ayubi-2-13|30993|মো. আরাফাত|Md Arafat|রাজবাড়ী (IAB)|IAB-Rajbari|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-B|Bed-05
288|Ayubi-2-14|Narail-14|তাহসিন জমাদ্দার|Tashin Jomddar|নড়াইল|Narail|WAMY-OSP|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-B|Bed-06
289|Ayubi-2-15|IAB0550|রাকিব|Rakib|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-B|Bed-07
290|Ayubi-2-16|401|মো. আলী নকীব|Md. Ali Noki|মুন্সীগঞ্জ (IAB)|IAB-Munshigonj|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-B|Bed-08
291|Ayubi-2-17|IAB0582|মো. জিসান মণ্ডল|Md. Jisan Mondol|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-B|Bed-09
292|Ayubi-2-18|Magura-101|মো. মুবিন হাসান তামিম|Md. Mubin Hasan Tamim|মাগুরা|Magura|WAMY-OSP|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-Common|Bed-01
293|Ayubi-2-19|BD-31758|আসাদুল্লাহ গালিব|Asadullah Galib|সাতক্ষীরা (IAB)|IAB-Satkhira|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-Common|Bed-02
294|Ayubi-2-20|EDAS 1661|ইমরুল কায়েস আরাফাত|Imrul Kayes Arafat|ময়মনসিংহ|Mymansing|EDAS|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-Common|Bed-03
295|Ayubi-2-21|ICNA|আবদুল ফায়াজ|Abdul Fayaz|কক্সবাজার|Cox's Bazar|ICNA|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-Common|Bed-04
296|Ayubi-2-22|IAB0545|জুনায়েদ ইসলাম রাহাত|Jonayed Islam Rahat|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-Common|Bed-05
297|Ayubi-2-23|BD-3729|আবির হোসেন|Abir Hosan|গাজীপুর (IAB)|IAB-Gazipur|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-Common|Bed-06
298|Ayubi-2-24|EDAS-1308|মো. রবিউল ইসলাম|Md. Robiul Islam|কুষ্টিয়া|Kustia|EDAS|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-Common|Bed-07
299|Ayubi-2-25|EDAS-226|মো. মিনহাজুল ইসলাম|Md Minhazul Islam|পটুয়াখালী|Patuakhali|EDAS|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-Common|Bed-08
300|Ayubi-2-26|31005|মো. ইমরান হাসান|Md. Imran Hasan|লালমনিরহাট (IAB)|IAB-Lalmonirhat|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-Common|Bed-09
301|Ayubi-2-27|N/A|ইয়ামিন হোসেন|Yamin Hossen|কুষ্টিয়া|Kustia|WAMY-OSP|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-Common|Bed-10
302|Ayubi-2-28|1202|মো. রবিউল ইসলাম|Md Robiul Islam|কুড়িগ্রাম (IAB)|IAB-Kurigram|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-Common|Bed-11
303|Ayubi-2-29|BD-31518|মবিনুর রহমান|Mobinur Rahman|সাতক্ষীরা (IAB)|IAB-Satkhira|IAB|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-Common|Bed-12
304|Ayubi-2-30|EDS-258|জাহিদ হাসান পার্থ|Zahid Hasan Partho|রাজশাহী|Rajshahi|EDS|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-Common|Bed-13
305|Ayubi-2-31|EDAS-1302|মো. হাবিব হোসেন|Md. Habib Hossain|কুষ্টিয়া|Kustia|EDAS|Ayubi-02|জুবায়ের মৃধা (DHD)|P5-Up-Common|Bed-14
306|Ayubi-3-1|EDAS-891|মো. খাদেমুল ইসলাম|Md. Khademul Islam|পঞ্চগড়|Panchagarh|EDAS|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-A|Bed-01
307|Ayubi-3-2|SKB 2490|আবদুল হাকিম|Abdul Hakim|জামালপুর|Jamalpur|SKB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-A|Bed-02
308|Ayubi-3-3|SKB 2714|ইয়াসির আরাফাত|Yeasir Arefat|কক্সবাজার|Cox's Bazar|SKB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-A|Bed-03
309|Ayubi-3-4|BD-31234|ওলিউল্লাহ|Oliullah|গাজীপুর (IAB)|IAB-Gazipur|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-A|Bed-04
310|Ayubi-3-5|BD-31295|আল সাদ|Al-Sayad|খুলনা (IAB)|IAB-Khulna|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-A|Bed-05
311|Ayubi-3-6|369|মিজানুর রহমান|Mijanur Rahman|মুন্সীগঞ্জ (IAB)|IAB-Munshigonj|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-A|Bed-06
312|Ayubi-3-7|N/A|হুজাইফা|Hujaifa|মেহেরপুর|Meherpur|WAMY-OSP|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-A|Bed-07
313|Ayubi-3-8|SKB 1704|তাসমিন আক্তার|Tasmin Akter|কক্সবাজার|Cox's Bazar|SKB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-B|Bed-01
314|Ayubi-3-9|IAB0560|আল আরাফ|Al Araf|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-B|Bed-02
315|Ayubi-3-10|BD-31616|মো. ফাহিম ইসলাম|Md Fahim Islam|খুলনা (IAB)|IAB-Khulna|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-B|Bed-03
316|Ayubi-3-11|31171|মো. ইব্রাহিম মিয়া|Md Ebrahim Mia|কুড়িগ্রাম (IAB)|IAB-Kurigram|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-B|Bed-04
317|Ayubi-3-12|31499|মো. রিফাত ইসলাম|Md. Rifat Islam|লালমনিরহাট (IAB)|IAB-Lalmonirhat|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-B|Bed-05
318|Ayubi-3-13|BD-31258|আসাদুজ্জামান|Asaduzzaman|সাতক্ষীরা (IAB)|IAB-Satkhira|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-B|Bed-06
319|Ayubi-3-14|Narail-47|শাকিব হাসান তামিম|Shakib Hasan Tamim|নড়াইল|Narail|WAMY-OSP|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-B|Bed-07
320|Ayubi-3-15|IAB0470|রিজভী রাইসুল|Rezvi Raisul|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-C|Bed-01
321|Ayubi-3-16|EDAS-1314|মো. ইমরান|Md. Imran|কুষ্টিয়া|Kustia|EDAS|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-C|Bed-02
322|Ayubi-3-17|SKB 2447|মোহাম্মদ ওমর ফারুক|Mohammad Omar Faruk|কক্সবাজার|Cox's Bazar|SKB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-C|Bed-03
323|Ayubi-3-18|Pabna-70|মো. মাহরাম খাঁ|Md. Mahram Kha|পাবনা|Pabna|WAMY-OSP|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-C|Bed-04
324|Ayubi-3-19|IAB0589|মো. রাকিব হাসান|Md. Rakib Hasan|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-C|Bed-05
325|Ayubi-3-20|Pabna-47|রামিম মাহমুদ|Ramim Mahmud|পাবনা|Pabna|WAMY-OSP|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-C|Bed-06
326|Ayubi-3-21|SKB 1557|আবদুল্লাহ আল মুনতাসির|Abdullah Al Muntasir|কক্সবাজার|Cox's Bazar|SKB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-Common|Bed-01
327|Ayubi-3-22|IAB0544|ফুয়াদ হাসান|Fuad Hasan|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-Common|Bed-02
328|Ayubi-3-23|30992|হোসেন|Hossain|রাজবাড়ী (IAB)|IAB-Rajbari|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-Common|Bed-03
329|Ayubi-3-24|EDAS-1373|মো. বিপুল|Md. Bipul|কুষ্টিয়া|Kustia|EDAS|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-Common|Bed-04
330|Ayubi-3-25|EDS-079|সবুজ আলী মণ্ডল|Shabuz Ali Mandal|রাজশাহী|Rajshahi|EDS|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-Common|Bed-05
331|Ayubi-3-26|580|মো. রিয়াজুল ইসলাম|Md. Riajul Islam|লালমনিরহাট (IAB)|IAB-Lalmonirhat|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-Common|Bed-06
332|Ayubi-3-27|02-23-058|সাঈদ বিন শোয়াইব|Sayed Bin Shoaib|চট্টগ্রাম (IAB)|IAB-Chattogram|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-Common|Bed-07
333|Ayubi-3-28|31002|মো. আবু বকর|Md. Abu Bokor|লালমনিরহাট (IAB)|IAB-Lalmonirhat|IAB|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-Common|Bed-08
334|Ayubi-3-29|22100210|ফাহিমুল হাসান|Fahimul Hasan|কুড়িগ্রাম|Kurigram|WAMY-OSP|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-Common|Bed-09
335|Ayubi-3-30|EDAS-1391|মো. হাফিজুর রহমান|Md Hafijur Rahman|রাজশাহী|Rajshahi|EDAS|Ayubi-03|মো. ফরহাদুজ্জামান (JSK)|P6-Down-Common|Bed-10
336|Ayubi-4-1|IAB0688|ইস্তামুল হক|Estamul Haque|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-A|Bed-01
337|Ayubi-4-2|EDAS-581|আলিফ বাবু|Alif Babu|লালমনিরহাট|Lalmonirhat|EDAS|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-A|Bed-02
338|Ayubi-4-3|SKB 2367|মোহাম্মদ মাসুদ পারভেজ|Mohammad Masud Parves|কক্সবাজার|Cox's Bazar|SKB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-A|Bed-03
339|Ayubi-4-4|BD-31031|সামিউল হাসান নাদিম|Samiul Hasan Nadim|গাজীপুর (IAB)|IAB-Gazipur|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-A|Bed-04
340|Ayubi-4-5|BD-31281|জুবায়ের শান্ত শেখ|Jubayr Santo Sekh|খুলনা (IAB)|IAB-Khulna|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-A|Bed-05
341|Ayubi-4-6|IAB0577|মো. আশিকুর রহমান|Md. Asikur Rahman|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-A|Bed-06
342|Ayubi-4-7|EDAS-1292|মো. জিম সরদার|Md. Jim Sordar|নওগাঁ|Naogaon|EDAS|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-A|Bed-07
343|Ayubi-4-8|BD-2816|সিয়াম|Siam|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-A|Bed-08
344|Ayubi-4-9|IAB0136|শিমুল বিশ্বাস|Shimul Biswas|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-B|Bed-01
345|Ayubi-4-10|BD-31445|মো. সাব্বির শেখ|Md.Sabbir Sheikh|খুলনা (IAB)|IAB-Khulna|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-B|Bed-02
346|Ayubi-4-11|31488|মো. হাসান আলী|Md Hasan Ali|কুড়িগ্রাম (IAB)|IAB-Kurigram|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-B|Bed-03
347|Ayubi-4-12|414|আসাদুল হাবিব|Asadul Habib|মুন্সীগঞ্জ (IAB)|IAB-Munshigonj|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-B|Bed-04
348|Ayubi-4-13|BD-31694|মো. মোহতাসিম বিল্লাহ|Md. Motashim Billah|সাতক্ষীরা (IAB)|IAB-Satkhira|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-B|Bed-05
349|Ayubi-4-14|ICNA 2262|মোহাম্মদ ইব্রাহিম|Mohammad Ibrahim|কক্সবাজার|Cox's Bazar|ICNA|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-B|Bed-06
350|Ayubi-4-15|BD-31284|মো. রমজান মুন্সী|Md.Romjan Munsi|খুলনা (IAB)|IAB-Khulna|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-B|Bed-07
351|Ayubi-4-16|31006|মো. নূর হাবিব|Md. Nur Habib|লালমনিরহাট (IAB)|IAB-Lalmonirhat|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-B|Bed-08
352|Ayubi-4-17|BD-2839|আল নূর ইসলাম|Al Noor Islam|কুমিল্লা (IAB)|IAB-Cumilla|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-Common|Bed-01
353|Ayubi-4-18|BD-31641|মো. আবদুল্লাহ আল মামুন|Md.Abdullah Al Mamun|খুলনা (IAB)|IAB-Khulna|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-Common|Bed-02
354|Ayubi-4-19|N/A|নূর মুহাম্মদ সাকিব|Nur Mummad Sakib|কিশোরগঞ্জ|Kishorganj|WAMY-OSP|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-Common|Bed-03
355|Ayubi-4-20|Bagura-46|মো. মিনহাজুর রহমান|Md. Minhazur Rahman|বগুড়া|Bogura|WAMY-OSP|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-Common|Bed-04
356|Ayubi-4-21|SKB 2461|আনোয়ার ইসলাম|Anowar Islam|কক্সবাজার|Cox's Bazar|SKB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-Common|Bed-05
357|Ayubi-4-22|IAB0548|মোবাশ্বির খান|Mobasshir Khan|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-Common|Bed-06
358|Ayubi-4-23|31440|সাজিদ সরদার|Sajid Sorder|রাজবাড়ী (IAB)|IAB-Rajbari|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-Common|Bed-07
359|Ayubi-4-24|Pabna-71|মাহিমা নুসরাত মিম|Mahima Nusrat Mim|পাবনা|Pabna|WAMY-OSP|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-Common|Bed-08
360|Ayubi-4-25|EDS-380|নাঈম হোসেন|Naim Hossain|রাজশাহী|Rajshahi|EDS|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-Common|Bed-09
361|Ayubi-4-26|30920|মো. রবিউল ইসলাম|Md. Robiul Islam|লালমনিরহাট (IAB)|IAB-Lalmonirhat|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-Common|Bed-10
362|Ayubi-4-27|BD-30961|সাব্বির আহমেদ|Sabbir Ahmed|গাজীপুর (IAB)|IAB-Gazipur|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-Common|Bed-11
363|Ayubi-4-28|378|মাহমুদুল হাসান মাসুদ|Mahmudul Hasan Masud|মুন্সীগঞ্জ (IAB)|IAB-Munshigonj|IAB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-Common|Bed-12
364|Ayubi-4-29|EDAS-423|মিশকাত খন্দকার|Mishkat Khandakar|বরগুনা|Barguna|EDAS|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-Common|Bed-13
365|Ayubi-4-30|SKB2829|শোয়াইব হাসান রোহান|Shoyaib Hasan Rohan|জয়পুরহাট|Joypurhat|SKB|Ayubi-04|আবদুল আল মামুন (WDH)|P6-Up-Common|Bed-14
    """

    private const val SUMAIYA_TSV = """
366|Sumaiya-1-1|N/A|স্নেহা আক্তার|Sneha Akter|বগুড়া|Bogura|WAMY-OSP|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-101|Bed-01
367|Sumaiya-1-2|SKB 2505|রাবিয়া জান্নাত বৃষ্টি|Rabia Jannat Bristy|জামালপুর|Jamalpur|SKB|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-101|Bed-02
368|Sumaiya-1-3|EDAS-1137|নুসাইবা চৌধুরী|Nusaiba Chaudhury|জয়পুরহাট|Joypurhat|EDAS|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-101|Bed-03
369|Sumaiya-1-4|EDS-462|তানিয়া খাতুন|Tania Khatun|পাবনা|Pabna|EDS|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-101|Bed-04
370|Sumaiya-1-5|EDS-339|শর্মিলা|Shormila|রাজশাহী|Rajshahi|EDS|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-101|Bed-05
371|Sumaiya-1-6|ICNA|আফিয়া আসিয়া|Afia Asia|কক্সবাজার|Cox's Bazar|ICNA|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-101|Bed-06
372|Sumaiya-1-7|EDAS-407|মোছা. আমেনা|Mst Amena|বরগুনা|Barguna|EDAS|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-101|Bed-07
373|Sumaiya-1-8|EDAS-1034|সিদরাতুল মুনতাহা|Sidratul Muntaha|দিনাজপুর|Dinajpur|EDAS|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-101|Bed-08
374|Sumaiya-1-9|IAB0539|সিনথিয়া খাতুন|Sinthia Khatun|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-102|Bed-01
375|Sumaiya-1-10|SKB2821|মারিয়াম জান্নাত মিমহা|Marium Jannat Mimha|জামালপুর|Jamalpur|SKB|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-102|Bed-02
376|Sumaiya-1-11|EDS-188|তাবাসসুম|Tabassum|ঝিনাইদহ|Jhenaidah|EDS|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-102|Bed-03
377|Sumaiya-1-12|N/A|জাকিয়া সুলতানা|Jakia Sultana|কিশোরগঞ্জ|Kishorganj|WAMY-OSP|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-102|Bed-04
378|Sumaiya-1-13|Meherpur-11|মাহি আক্তার|Mahi Akthar|মেহেরপুর|Meherpur|WAMY-OSP|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-102|Bed-05
379|Sumaiya-1-14|EDAS-1251|মেহজাবিন জান্নাত|Mehjabin Jannat|নওগাঁ|Naogaon|EDAS|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-102|Bed-06
380|Sumaiya-1-15|EDAS-1531|নয়ন তারা খানম|Nayan Tara Khanom|নরসিংদী|Narsingdi|EDAS|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-102|Bed-07
381|Sumaiya-1-16|EDAS-869|উম্মে হানি|Umme Hani|পঞ্চগড়|Panchagarh|EDAS|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-102|Bed-08
382|Sumaiya-1-17|EDS-263|ঈশা খাতুন|Isha Khatun|রাজশাহী|Rajshahi|EDS|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-103|Bed-01
383|Sumaiya-1-18|EDAS-89|হুমায়রা খাতুন|Humayra Khatun|সাতক্ষীরা|Satkhira|EDAS|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-103|Bed-02
384|Sumaiya-1-19|EDAS-983|শাহনাজ পারভীন|Shahnaj Parvin|ঠাকুরগাঁও|Thakurgaon|EDAS|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-103|Bed-03
385|Sumaiya-1-20|ICNA 156|আলিনা আক্তার এনি|Alina Akter Any|কক্সবাজার|Cox's Bazar|ICNA|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-103|Bed-04
386|Sumaiya-1-21|N/A|মায়শা আক্তার দীঘি|Maysha Akter Dighi|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Sumaiya-01|তাকিয়া রুফাইদা|Rajo-103|Bed-05
387|Sumaiya-2-1|EDAS-1009|আয়েশা সিদ্দিকা|Ayesha Siddika|দিনাজপুর|Dinajpur|EDAS|Sumaiya-02|বুশরা নুসাইবা|Rajo-103|Bed-06
388|Sumaiya-2-2|SKB 2507|রুহানা আক্তার|Ruhana Akter|জামালপুর|Jamalpur|SKB|Sumaiya-02|বুশরা নুসাইবা|Rajo-103|Bed-07
389|Sumaiya-2-3|EDAS-048|আমেনা খাতুন|Amena Khatun|খুলনা|Khulna|EDAS|Sumaiya-02|বুশরা নুসাইবা|Rajo-103|Bed-08
390|Sumaiya-2-4|Pabna-33|মোছা. জান্নাতুল মাওয়া|Mst. Jannatul Mawa|পাবনা|Pabna|WAMY-OSP|Sumaiya-02|বুশরা নুসাইবা|Rajo-104|Bed-01
391|Sumaiya-2-5|EDS-74|নুসাইবা ফারহিন|Nusaiba Farhin|রাজশাহী|Rajshahi|EDS|Sumaiya-02|বুশরা নুসাইবা|Rajo-104|Bed-02
392|Sumaiya-2-6|EDAS-966|সুরাইয়া আক্তার|Suraiya Akter|ঠাকুরগাঁও|Thakurgaon|EDAS|Sumaiya-02|বুশরা নুসাইবা|Rajo-104|Bed-03
393|Sumaiya-2-7|Bagura-72|এনি খাতুন|Annie Khatun|বগুড়া|Bogura|WAMY-OSP|Sumaiya-02|বুশরা নুসাইবা|Rajo-104|Bed-04
394|Sumaiya-2-8|EDAS-1022|রুকু খাতুন|Ruku Khatun|দিনাজপুর|Dinajpur|EDAS|Sumaiya-02|বুশরা নুসাইবা|Rajo-104|Bed-05
395|Sumaiya-2-9|N/A|মুনতাহা আক্তার মিম|Muntaha Akter Mim|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Sumaiya-02|বুশরা নুসাইবা|Rajo-104|Bed-06
396|Sumaiya-2-10|IAB0555|মোছা. ফাতেমাতুজ জাহরা|Mst. Fatamatuj Jahra|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Sumaiya-02|বুশরা নুসাইবা|Rajo-104|Bed-07
397|Sumaiya-2-11|BD-0031|খাদিজা খাতুন|Khadiza Khatun|যশোর|Jashore|WAMY-BD|Sumaiya-02|বুশরা নুসাইবা|Rajo-104|Bed-08
398|Sumaiya-2-12|EDAS-1130|ফাতেমাতুজ জাহরা|Fatematuj Jahra|জয়পুরহাট|Joypurhat|EDAS|Sumaiya-02|বুশরা নুসাইবা|Rajo-104|Bed-09
399|Sumaiya-2-13|N/A|পুষ্প আক্তার আমেনা|Puspo Akter Amena|কিশোরগঞ্জ|Kishorganj|WAMY-OSP|Sumaiya-02|বুশরা নুসাইবা|Rajo-105|Bed-01
400|Sumaiya-2-14|EDAS 1667|তাসমিম আক্তার|Tasmim Akter|ময়মনসিংহ|Mymansing|EDAS|Sumaiya-02|বুশরা নুসাইবা|Rajo-105|Bed-02
401|Sumaiya-2-15|EDAS-1259|মোছা. যূথী|Mst. Juthi|নওগাঁ|Naogaon|EDAS|Sumaiya-02|বুশরা নুসাইবা|Rajo-105|Bed-03
402|Sumaiya-2-16|EDAS-1586|নাবিলা আক্তার নওরিন|Nabila Akter Nourin|নরসিংদী|Narsingdi|EDAS|Sumaiya-02|বুশরা নুসাইবা|Rajo-105|Bed-04
403|Sumaiya-2-17|EDAS-873|সুমাইয়া আক্তার রিমু|Sumaiya Akter Rimu|পঞ্চগড়|Panchagarh|EDAS|Sumaiya-02|বুশরা নুসাইবা|Rajo-105|Bed-05
404|Sumaiya-2-18|EDS-265|নূরে হুজাইফা হাফসা|Noore Husaifa Hafsa|রাজশাহী|Rajshahi|EDS|Sumaiya-02|বুশরা নুসাইবা|Rajo-105|Bed-06
405|Sumaiya-2-19|EDAS- 1709|রিফা সুলতানা|Rifa Sultana|সাতক্ষীরা|Satkhira|EDAS|Sumaiya-02|বুশরা নুসাইবা|Rajo-105|Bed-07
406|Sumaiya-2-20|EDAS-378|মোছা. খাদিজা|Mst Khadiza|বরগুনা|Barguna|EDAS|Sumaiya-02|বুশরা নুসাইবা|Rajo-105|Bed-08
407|Sumaiya-2-21|SKB 2732|শারমিন আক্তার|Sharmin Akter|কক্সবাজার|Cox's Bazar|SKB|Sumaiya-02|বুশরা নুসাইবা|Rajo-105|Bed-09
408|Sumaiya-3-1|SKB 2324|মারফুয়া জাহান জান্নাত|Marfua Jahan Jannat|জামালপুর|Jamalpur|SKB|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-106|Bed-01
409|Sumaiya-3-2|SKB 2512|তানিয়া আক্তার|Tania Akter|জামালপুর|Jamalpur|SKB|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-106|Bed-02
410|Sumaiya-3-3|EDAS-1536|তানজিনা আক্তার|Tanzina Akter|নরসিংদী|Narsingdi|EDAS|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-106|Bed-03
411|Sumaiya-3-4|EDAS-813|নুসরাত জাহান|Nusrat Jahan|পঞ্চগড়|Panchagarh|EDAS|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-106|Bed-04
412|Sumaiya-3-5|EDAS- 1675|মারিয়া আক্তার|Maria Akter|সাতক্ষীরা|Satkhira|EDAS|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-106|Bed-05
413|Sumaiya-3-6|EDAS-385|রহিমা খাতুন|Rahima Khatun|বরগুনা|Barguna|EDAS|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-106|Bed-06
414|Sumaiya-3-7|EDS-453|মোছা. স্নেহা আক্তার|Mst. Sneha Akter|বগুড়া|Bogura|EDS|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-106|Bed-07
415|Sumaiya-3-8|EDAS-1026|তাসনিম আক্তার|Tasnim Akter|দিনাজপুর|Dinajpur|EDAS|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-106|Bed-08
416|Sumaiya-3-9|N/A|হেলেনা আক্তার|Helena Akter|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-Common|Bed-01
417|Sumaiya-3-10|IAB0679|হাসনাহেনা মিম|Hasnahena Mim|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-Common|Bed-02
418|Sumaiya-3-11|BD-0172|মায়শা বিনতে জান্নাতি|Maisha Binta Jannaty|যশোর|Jashore|WAMY-BD|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-Common|Bed-03
419|Sumaiya-3-12|EDAS-1157|সোহাগী খাতুন|Shohagi Khatun|জয়পুরহাট|Joypurhat|EDAS|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-Common|Bed-04
420|Sumaiya-3-13|EDAS-1383|মোছা. লামিয়া খাতুন|Mst. Lamiya Khatun|কুষ্টিয়া|Kustia|EDAS|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-Common|Bed-05
421|Sumaiya-3-14|EDAS-1220|মোছা. মারিয়াম|Mst. Mariam|নওগাঁ|Naogaon|EDAS|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-Common|Bed-06
422|Sumaiya-3-15|EDAS-1262|নুসরাত জাহান মিম|Nusrat Jahan Mim|নওগাঁ|Naogaon|EDAS|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-Common|Bed-07
423|Sumaiya-3-16|EDAS-823|নুসরাত জাহান|Nusrat Jahan|পঞ্চগড়|Panchagarh|EDAS|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-Common|Bed-08
424|Sumaiya-3-17|EDS-256|নুসরাত জাহান মিম|Nusrat Jahan Mim|রাজশাহী|Rajshahi|EDS|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-Common|Bed-09
425|Sumaiya-3-18|EDS-322|সামিয়া আক্তার মায়া|Samia Akter Maya|রাজশাহী|Rajshahi|EDS|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-Common|Bed-10
426|Sumaiya-3-19|EDAS- 1730|হালিমাতুস সাদিয়া|Halimatus Sadia|সাতক্ষীরা|Satkhira|EDAS|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-Common|Bed-11
427|Sumaiya-3-20|22100013|আনিশা আক্তার জুঁই|Anisha Akter Jui|চাঁপাইনবাবগঞ্জ|Chapainawabganj|WAMY-OSP|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-Common|Bed-12
428|Sumaiya-3-21|SKB 1558|সিদরাতুল মুনতাহা|Sidratul Muntaha|কক্সবাজার|Cox's Bazar|SKB|Sumaiya-03|জেবা আনিকা নিঝু|Rajo-Common|Bed-13
429|Sumaiya-4-1|SKB 2325|মারিয়া আক্তার|Maria Akter|জামালপুর|Jamalpur|SKB|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-202|Bed-01
430|Sumaiya-4-2|BD-0079|সামিহা হক|Samiha Haque|যশোর|Jashore|WAMY-BD|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-202|Bed-02
431|Sumaiya-4-3|EDAS-1560|আল বীনা|Al Bina|নরসিংদী|Narsingdi|EDAS|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-202|Bed-03
432|Sumaiya-4-4|EDAS-871|ফারদিন আক্তার|Fardin Akter|পঞ্চগড়|Panchagarh|EDAS|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-202|Bed-04
433|Sumaiya-4-5|EDAS-984|নুসরাত জাহান লামিয়া|Nusrat Jahan Lamia|ঠাকুরগাঁও|Thakurgaon|EDAS|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-202|Bed-05
434|Sumaiya-4-6|EDAS-402|ফাতেমা আক্তার|Fatema Akter|বরগুনা|Barguna|EDAS|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-202|Bed-06
435|Sumaiya-4-7|EDAS-1027|তাহসিন আক্তার|Tahsin Akter|দিনাজপুর|Dinajpur|EDAS|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-202|Bed-07
436|Sumaiya-4-8|N/A|ফেরদৌসী|Ferdousi|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-203|Bed-01
437|Sumaiya-4-9|SKB2813|রিয়া জান্নাত স্বর্ণা|Ria Jannat Shorna|জামালপুর|Jamalpur|SKB|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-203|Bed-02
438|Sumaiya-4-10|EDS-168|আনিকা খাতুন|Anika Khatun|ঝিনাইদহ|Jhenaidah|EDS|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-203|Bed-03
439|Sumaiya-4-11|EDAS-1175|মাহি|Mahi|জয়পুরহাট|Joypurhat|EDAS|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-203|Bed-04
440|Sumaiya-4-12|Magura-63|রিয়া খাতুন|Ria Khatun|মাগুরা|Magura|WAMY-OSP|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-203|Bed-05
441|Sumaiya-4-13|EDAS-1243|ফাতেমা জান্নাত প্রার্থনা|Fatema Jannat Prarthona|নওগাঁ|Naogaon|EDAS|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-203|Bed-06
442|Sumaiya-4-14|Narail-75|মোছা. রুকাইয়া|Ms. Rukaiya|নড়াইল|Narail|WAMY-OSP|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-203|Bed-07
443|Sumaiya-4-15|EDAS-825|ফারজানা আক্তার|Farjana Akter|পঞ্চগড়|Panchagarh|EDAS|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-204|Bed-01
444|Sumaiya-4-16|EDS-262|জান্নাতুল ফেরদৌস|Jannatul Ferdous|রাজশাহী|Rajshahi|EDS|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-204|Bed-02
445|Sumaiya-4-17|EDAS-85|লামিয়া বিনতে সাদেক|Lamia Bint Sadeque|সাতক্ষীরা|Satkhira|EDAS|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-204|Bed-03
446|Sumaiya-4-18|EDS-395|সাদিয়া খাতুন|Sadia Khatun|সিরাজগঞ্জ|Sirajganj|EDS|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-204|Bed-04
447|Sumaiya-4-19|N/A|জান্নাতি|Jannati|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-204|Bed-05
448|Sumaiya-4-20|N/A|মারিয়াম আক্তার|Mariam Akter|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-204|Bed-06
449|Sumaiya-4-21|IAB0694|তানিয়া আক্তার তমা|Tania Aktar Toma|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Sumaiya-04|ফারহানা ইয়াসমিন নয়ন|Rajo-204|Bed-07
    """

    private const val UMM_AYMAN_TSV = """
450|Umme Ayman-1-1|IAB0603|মোছা. কনা খাতুন|Mst. Kona Khatun|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-A|Bed-01
451|Umme Ayman-1-2|EDAS-1382|মোছা. ফাতেমা খাতুন|Mst. Fatema Khatun|কুষ্টিয়া|Kustia|EDAS|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-A|Bed-02
452|Umme Ayman-1-3|EDAS-724|জান্নাতুল মাওয়া|Zannatul Mawa|লালমনিরহাট|Lalmonirhat|EDAS|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-A|Bed-03
453|Umme Ayman-1-4|EDAS 1639|আফসানা মিম আফিয়া|Afsana Mim Afia|ময়মনসিংহ|Mymansing|EDAS|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-A|Bed-04
454|Umme Ayman-1-5|EDAS-335|ইমা আক্তার|Ema Akter|নরসিংদী|Narsingdi|EDAS|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-A|Bed-05
455|Umme Ayman-1-6|EDAS-818|তানজিলা বেগম|Tanjila Begum|পঞ্চগড়|Panchagarh|EDAS|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-A|Bed-06
456|Umme Ayman-1-7|EDS-391|মোছা. আসিয়া খাতুন|Mst Asia Khatun|সিরাজগঞ্জ|Sirajganj|EDS|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-A|Bed-07
457|Umme Ayman-1-8|IAB0649|মোছা. জান্নাতি খাতুন|Mst. Zannati Khatun|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-A|Bed-08
458|Umme Ayman-1-9|22100174|আয়েশা খাতুন|Aysa Khatun|কুড়িগ্রাম|Kurigram|WAMY-OSP|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-A|Bed-09
459|Umme Ayman-1-10|ICNA 39|শফিকা হুসেন মাহি|Safika Hus Mahi|কক্সবাজার|Cox's Bazar|ICNA|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-A|Bed-10
460|Umme Ayman-1-11|SKB 2463|জান্নাতুল বাকিয়া|Zannatul Bakiya|কক্সবাজার|Cox's Bazar|SKB|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-B|Bed-01
461|Umme Ayman-1-12|BD-0054|সিনথিয়া রেজা|Sinthia Reza|যশোর|Jashore|WAMY-BD|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-B|Bed-02
462|Umme Ayman-1-13|Pabna-44|মোছা. তাসমিম আক্তার রাকা|Mst.Tasmim Akter Raka|পাবনা|Pabna|WAMY-OSP|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-B|Bed-03
463|Umme Ayman-1-14|ICNA|শারমিন সুলতানা রিমা|Sarmin Sultana Rima|কক্সবাজার|Cox's Bazar|ICNA|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-B|Bed-04
464|Umme Ayman-1-15|IAB0637|মোছা. মিম খাতুন|Mst. Mim Khatun|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-B|Bed-05
465|Umme Ayman-1-16|Magura-95|রাবেয়া আক্তার স্বর্ণা|Rabea Akter Shorna|মাগুরা|Magura|WAMY-OSP|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-B|Bed-06
466|Umme Ayman-1-17|EDAS-1530|খাদিজা|Khadiza|নরসিংদী|Narsingdi|EDAS|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-B|Bed-07
467|Umme Ayman-1-18|EDAS- 1725|রুবাইয়া রাজ্জাক রোজা|Rubaiya Razzak Roza|সাতক্ষীরা|Satkhira|EDAS|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-B|Bed-08
468|Umme Ayman-1-19|ICNA 143|জেরিন আক্তার|Jerin Akter|কক্সবাজার|Cox's Bazar|ICNA|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-B|Bed-09
469|Umme Ayman-1-20|EDAS-1032|লায়মা আক্তার|Layma Akter|দিনাজপুর|Dinajpur|EDAS|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-B|Bed-10
470|Umme Ayman-1-21|22100188|জান্নাতি আক্তার চাঁদনী|Jannati Akter Chadni|কুড়িগ্রাম|Kurigram|WAMY-OSP|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-B|Bed-11
471|Umme Ayman-1-22|N/A|সাইমা|Saima|রাজশাহী|Rajshahi|WAMY-OSP|Umme Ayman-01|লাবিবা তাবাসসুম|Golden-2-B|Bed-12
472|Umme Ayman-2-1|EDAS-1168|ইসরাত জাহান|Israt Jahan|জয়পুরহাট|Joypurhat|EDAS|Umme Ayman-02|ফাহমিদা আক্তার|Golden-2-C|Bed-01
473|Umme Ayman-2-2|EDAS-563|বিলকিস বেগম|Bilkis Begum|লালমনিরহাট|Lalmonirhat|EDAS|Umme Ayman-02|ফাহমিদা আক্তার|Golden-2-C|Bed-02
474|Umme Ayman-2-3|Magura-27|মেঘলা খাতুন|Meghla Khatun|মাগুরা|Magura|WAMY-OSP|Umme Ayman-02|ফাহমিদা আক্তার|Golden-2-C|Bed-03
475|Umme Ayman-2-4|EDAS-1208|মোছা. রুবাইয়া|Mst. Rubaya|নওগাঁ|Naogaon|EDAS|Umme Ayman-02|ফাহমিদা আক্তার|Golden-2-C|Bed-04
476|Umme Ayman-2-5|EDAS-1529|সওদা|Sawda|নরসিংদী|Narsingdi|EDAS|Umme Ayman-02|ফাহমিদা আক্তার|Golden-2-C|Bed-05
477|Umme Ayman-2-6|EDAS-60|সিনথিয়া|Sinthiha|সাতক্ষীরা|Satkhira|EDAS|Umme Ayman-02|ফাহমিদা আক্তার|Golden-2-C|Bed-06
478|Umme Ayman-2-7|EDAS-974|মিসরাত জাহান|Misrat Jahan|ঠাকুরগাঁও|Thakurgaon|EDAS|Umme Ayman-02|ফাহমিদা আক্তার|Golden-2-C|Bed-07
479|Umme Ayman-2-8|SKB 2625|মুন্নি আক্তার|Munni Akter|জামালপুর|Jamalpur|SKB|Umme Ayman-02|ফাহমিদা আক্তার|Golden-2-C|Bed-08
480|Umme Ayman-2-9|EDAS-578|সূচনা|Shuchona|লালমনিরহাট|Lalmonirhat|EDAS|Umme Ayman-02|ফাহমিদা আক্তার|Golden-2-C|Bed-09
481|Umme Ayman-2-10|EDAS-870|মিম আক্তার|Mim Akter|পঞ্চগড়|Panchagarh|EDAS|Umme Ayman-02|ফাহমিদা আক্তার|Golden-2-C|Bed-10
482|Umme Ayman-2-11|EDAS-1036|আফরোজা আক্তার|Afroja Akter|দিনাজপুর|Dinajpur|EDAS|Umme Ayman-02|ফাহমিদা আক্তার|Golden-2-C|Bed-11
483|Umme Ayman-2-12|EDAS 1650|ফারিহা আক্তার|Fariha Akter|ময়মনসিংহ|Mymansing|EDAS|Umme Ayman-02|ফাহমিদা আক্তার|Golden-3-A|Bed-01
484|Umme Ayman-2-13|EDS-082|নাসরিন খাতুন|Nasrin Khatun|রাজশাহী|Rajshahi|EDS|Umme Ayman-02|ফাহমিদা আক্তার|Golden-3-A|Bed-02
485|Umme Ayman-2-14|SKB 2729|তাসলিমা আক্তার|Taslima Akter|কক্সবাজার|Cox's Bazar|SKB|Umme Ayman-02|ফাহমিদা আক্তার|Golden-3-A|Bed-03
486|Umme Ayman-2-15|BD-0181|সুমাইয়া আক্তার|Sumiya Akter|যশোর|Jashore|WAMY-BD|Umme Ayman-02|ফাহমিদা আক্তার|Golden-3-A|Bed-04
487|Umme Ayman-2-16|Narail-46|মিম খাতুন|Mim Khatun|নড়াইল|Narail|WAMY-OSP|Umme Ayman-02|ফাহমিদা আক্তার|Golden-3-A|Bed-05
488|Umme Ayman-2-17|EDAS-245|আমেনা বেগম|Amena Begum|পটুয়াখালী|Patuakhali|EDAS|Umme Ayman-02|ফাহমিদা আক্তার|Golden-3-A|Bed-06
489|Umme Ayman-2-18|EDAS- 1727|সুমাইয়া সুলতানা|Sumaiya Sultana|সাতক্ষীরা|Satkhira|EDAS|Umme Ayman-02|ফাহমিদা আক্তার|Golden-3-A|Bed-07
490|Umme Ayman-2-19|ICNA|নাহিদা আক্তার|Nahida Akter|কক্সবাজার|Cox's Bazar|ICNA|Umme Ayman-02|ফাহমিদা আক্তার|Golden-3-A|Bed-08
491|Umme Ayman-2-20|IAB0166|সাবরিনা আক্তার সীতা|Sabrina Akther Sheta|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Umme Ayman-02|ফাহমিদা আক্তার|Golden-3-A|Bed-09
492|Umme Ayman-2-21|22100189|মায়শা সাবিহা|Maysha Sabiha|কুড়িগ্রাম|Kurigram|WAMY-OSP|Umme Ayman-02|ফাহমিদা আক্তার|Golden-3-A|Bed-10
493|Umme Ayman-2-22|EDS-377|মোছা. শিমলা|Mst.Shimla|রাজশাহী|Rajshahi|EDS|Umme Ayman-02|ফাহমিদা আক্তার|Golden-3-A|Bed-11
494|Umme Ayman-3-1|EDAS-045|মরিয়ম|Moriyam|খুলনা|Khulna|EDAS|Umme Ayman-03|মৌলি ইসলাম|Golden-3-B|Bed-01
495|Umme Ayman-3-2|EDAS-587|মিম আক্তার|Mim Akter|লালমনিরহাট|Lalmonirhat|EDAS|Umme Ayman-03|মৌলি ইসলাম|Golden-3-B|Bed-02
496|Umme Ayman-3-3|Magura-90|মোছা. ফারজানা খাতুন|Mst. Farjana Khatun|মাগুরা|Magura|WAMY-OSP|Umme Ayman-03|মৌলি ইসলাম|Golden-3-B|Bed-03
497|Umme Ayman-3-4|EDAS-1212|মারিয়াম আক্তার জিম|Mariam Akter Jim|নওগাঁ|Naogaon|EDAS|Umme Ayman-03|মৌলি ইসলাম|Golden-3-B|Bed-04
498|Umme Ayman-3-5|EDAS-796|দীপা মনি|Dipa Moni|পঞ্চগড়|Panchagarh|EDAS|Umme Ayman-03|মৌলি ইসলাম|Golden-3-B|Bed-05
499|Umme Ayman-3-6|SKB 2607|আঁখি খাতুন|Akhi Khatun|সাতক্ষীরা|Satkhira|SKB|Umme Ayman-03|মৌলি ইসলাম|Golden-3-B|Bed-06
500|Umme Ayman-3-7|SKB 2467|উম্মে সালমা|Umme Salma|কক্সবাজার|Cox's Bazar|SKB|Umme Ayman-03|মৌলি ইসলাম|Golden-3-B|Bed-07
501|Umme Ayman-3-8|BD-0141|ঊর্মি আক্তার মুনিয়া|Urmi Akter Muniya|যশোর|Jashore|WAMY-BD|Umme Ayman-03|মৌলি ইসলাম|Golden-3-B|Bed-08
502|Umme Ayman-3-9|EDAS-827|আয়েশা সিদ্দিকা|Ayesha Siddika|পঞ্চগড়|Panchagarh|EDAS|Umme Ayman-03|মৌলি ইসলাম|Golden-3-B|Bed-09
503|Umme Ayman-3-10|EDS-400|মাহমুদা|Mahmuda|রাজশাহী|Rajshahi|EDS|Umme Ayman-03|মৌলি ইসলাম|Golden-3-B|Bed-10
504|Umme Ayman-3-11|IAB0567|জান্নাতুল মাওয়া ঊর্মি|Jannatul Mawa Urmi|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Umme Ayman-03|মৌলি ইসলাম|Golden-3-B|Bed-11
505|Umme Ayman-3-12|EDAS 1652|নাদিবা আক্তার|Nadiba Akter|ময়মনসিংহ|Mymansing|EDAS|Umme Ayman-03|মৌলি ইসলাম|Golden-3-B|Bed-12
506|Umme Ayman-3-13|SKB 2469|তায়েবা আক্তার|Tayeba Akter|কক্সবাজার|Cox's Bazar|SKB|Umme Ayman-03|মৌলি ইসলাম|Golden-3-C|Bed-01
507|Umme Ayman-3-14|ICNA|তাজকিয়া আক্তার|Tazkia Akter|কক্সবাজার|Cox's Bazar|ICNA|Umme Ayman-03|মৌলি ইসলাম|Golden-3-C|Bed-02
508|Umme Ayman-3-15|BD-0192|তানিশা ইয়াসমিন|Tanisha Yasmin|যশোর|Jashore|WAMY-BD|Umme Ayman-03|মৌলি ইসলাম|Golden-3-C|Bed-03
509|Umme Ayman-3-16|EDAS-1527|তানহা|Tanha|নরসিংদী|Narsingdi|EDAS|Umme Ayman-03|মৌলি ইসলাম|Golden-3-C|Bed-04
510|Umme Ayman-3-17|Rajshahi-287|পড়শী|Porshi|রাজশাহী|Rajshahi|WAMY-OSP|Umme Ayman-03|মৌলি ইসলাম|Golden-3-C|Bed-05
511|Umme Ayman-3-18|ICNA|ফারজানা ইয়াসমিন প্রমি|Farjana Yeasmin Prome|কক্সবাজার|Cox's Bazar|ICNA|Umme Ayman-03|মৌলি ইসলাম|Golden-3-C|Bed-06
512|Umme Ayman-3-19|SKB 2373|জান্নাত আরিফা ইসমা|Zannat Arifa Isma|কক্সবাজার|Cox's Bazar|SKB|Umme Ayman-03|মৌলি ইসলাম|Golden-3-C|Bed-07
513|Umme Ayman-3-20|BD-0174|মারিয়া খাতুন|Mariya Khatun|যশোর|Jashore|WAMY-BD|Umme Ayman-03|মৌলি ইসলাম|Golden-3-C|Bed-08
514|Umme Ayman-3-21|EDAS 1646|আখিয়া ইসলাম রাত্রি|Akheya Islam Ratri|ময়মনসিংহ|Mymansing|EDAS|Umme Ayman-03|মৌলি ইসলাম|Golden-3-C|Bed-09
515|Umme Ayman-3-22|SKB 1580|চাহিরা আক্তার মুন্নি|Chahira Akter Munni|কক্সবাজার|Cox's Bazar|SKB|Umme Ayman-03|মৌলি ইসলাম|Golden-3-C|Bed-10
516|Umme Ayman-4-1|EDAS-1338|বুশরা|Bushra|কুষ্টিয়া|Kustia|EDAS|Umme Ayman-04|সায়েমা আফনান|Golden-4-A|Bed-01
517|Umme Ayman-4-2|EDAS-662|মারিয়াম নোভা|Maryam Nova|লালমনিরহাট|Lalmonirhat|EDAS|Umme Ayman-04|সায়েমা আফনান|Golden-4-A|Bed-02
518|Umme Ayman-4-3|Magura-109|সাইমা নুরিয়া|Saima Nuria|মাগুরা|Magura|WAMY-OSP|Umme Ayman-04|সায়েমা আফনান|Golden-4-A|Bed-03
519|Umme Ayman-4-4|EDAS-1230|সওদা মনি|Sowda Muni|নওগাঁ|Naogaon|EDAS|Umme Ayman-04|সায়েমা আফনান|Golden-4-A|Bed-04
520|Umme Ayman-4-5|EDAS-882|রিমু আক্তার|Rimu Akter|পঞ্চগড়|Panchagarh|EDAS|Umme Ayman-04|সায়েমা আফনান|Golden-4-A|Bed-05
521|Umme Ayman-4-6|EDAS- 1688|রুবিনা আক্তার|Rubina Akter|সাতক্ষীরা|Satkhira|EDAS|Umme Ayman-04|সায়েমা আফনান|Golden-4-A|Bed-06
522|Umme Ayman-4-7|N/A|খাদিজা|Khadiza|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Umme Ayman-04|সায়েমা আফনান|Golden-4-A|Bed-07
523|Umme Ayman-4-8|N/A|জান্নাত|Jannat|কিশোরগঞ্জ|Kishorganj|WAMY-OSP|Umme Ayman-04|সায়েমা আফনান|Golden-4-A|Bed-08
524|Umme Ayman-4-9|ICNA 118|আফসানা জান্নাত নূরী|Afsana Jannat Nuri|কক্সবাজার|Cox's Bazar|ICNA|Umme Ayman-04|সায়েমা আফনান|Golden-4-A|Bed-09
525|Umme Ayman-4-10|ICNA 40|সুমাইয়া সুলতানা|Sumaiya Sultana|কক্সবাজার|Cox's Bazar|ICNA|Umme Ayman-04|সায়েমা আফনান|Golden-4-A|Bed-10
526|Umme Ayman-4-11|SKB 2747|মোছা. মিমু|Mst Mimu|জামালপুর|Jamalpur|SKB|Umme Ayman-04|সায়েমা আফনান|Golden-4-B|Bed-01
527|Umme Ayman-4-12|EDS-122|নুসরাত জাহান সুমাইয়া|Nusrat Jahan Sumaiya|নাটোর|Natore|EDS|Umme Ayman-04|সায়েমা আফনান|Golden-4-B|Bed-02
528|Umme Ayman-4-13|ICNA 68|মরিয়ম জাহান মুবাশ্বিরা|Morium Jahan Mubasshara|কক্সবাজার|Cox's Bazar|ICNA|Umme Ayman-04|সায়েমা আফনান|Golden-4-B|Bed-03
529|Umme Ayman-4-14|SKB 2280|হুমায়রা সুলতানা|Humyra Sultana|কক্সবাজার|Cox's Bazar|SKB|Umme Ayman-04|সায়েমা আফনান|Golden-4-B|Bed-04
530|Umme Ayman-4-15|N/A|মিফতাহুল জান্নাত|Miftahul Jannat|লালমনিরহাট|Lalmonirhat|WAMY-OSP|Umme Ayman-04|সায়েমা আফনান|Golden-4-B|Bed-05
531|Umme Ayman-4-16|EDAS-1528|জান্নাতুল নাঈমা তামান্না|Jannnatul Naima Tamanna|নরসিংদী|Narsingdi|EDAS|Umme Ayman-04|সায়েমা আফনান|Golden-4-B|Bed-06
532|Umme Ayman-4-17|EDAS- 1694|তাহিয়া তাবাসসুম|Tahia Tabassum|সাতক্ষীরা|Satkhira|EDAS|Umme Ayman-04|সায়েমা আফনান|Golden-4-B|Bed-07
533|Umme Ayman-4-18|SKB 2445|আলিসা ওয়াফা মাহি|Alisa Wafa Mahi|কক্সবাজার|Cox's Bazar|SKB|Umme Ayman-04|সায়েমা আফনান|Golden-4-B|Bed-08
534|Umme Ayman-4-19|EDAS-1021|নূর নাহার খাতুন|Noor Nahar Khatun|দিনাজপুর|Dinajpur|EDAS|Umme Ayman-04|সায়েমা আফনান|Golden-4-B|Bed-09
535|Umme Ayman-4-20|N/A|তামান্না আক্তার|Tamanna Akter|কিশোরগঞ্জ|Kishorganj|WAMY-OSP|Umme Ayman-04|সায়েমা আফনান|Golden-4-B|Bed-10
536|Umme Ayman-4-21|N/A|মোছা. সাফায়াতুন বিনতে শাফা|Mst. Safayatun Binty Shafa|নওগাঁ|Naogaon|WAMY-OSP|Umme Ayman-04|সায়েমা আফনান|Golden-4-B|Bed-11
537|Umme Ayman-4-22|Magura-55|মারিয়াম খাতুন|Mariam Khatun|মাগুরা|Magura|WAMY-OSP|Umme Ayman-04|সায়েমা আফনান|Golden-4-B|Bed-12
538|Umme Ayman-4-23|Magura-69|ফাতেমা খাতুন|Fatema Khatun|মাগুরা|Magura|WAMY-OSP|Umme Ayman-04|সায়েমা আফনান|Golden-4-B|Bed-13
    """

    private const val UMM_SAAD_TSV = """
539|Umme Sa'ad-1-1|N/A|তাসনিয়া তমা|Tasniya Toma|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-4-C|Bed-01
540|Umme Sa'ad-1-2|IAB0628|মোছা. আতিয়া খাতুন|Mst. Atiya Khatun|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-4-C|Bed-02
541|Umme Sa'ad-1-3|EDS-381|সুমাইয়া আক্তার|Sumaiya Akter|রাজশাহী|Rajshahi|EDS|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-4-C|Bed-03
542|Umme Sa'ad-1-4|SKB 1564|জান্নাতুল ফারিহা|Jannatul Fariha|কক্সবাজার|Cox's Bazar|SKB|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-4-C|Bed-04
543|Umme Sa'ad-1-5|N/A|শাবনূর|Shabnur|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-4-C|Bed-05
544|Umme Sa'ad-1-6|22100169|মল্লিকা আক্তার|Mollika Akter|কুড়িগ্রাম|Kurigram|WAMY-OSP|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-4-C|Bed-06
545|Umme Sa'ad-1-7|Meherpur-63|সাদিয়া খাতুন|Sadia Khatun|মেহেরপুর|Meherpur|WAMY-OSP|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-4-C|Bed-07
546|Umme Sa'ad-1-8|ICNA|রনা আক্তার|Rona Akter|কক্সবাজার|Cox's Bazar|ICNA|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-4-C|Bed-08
547|Umme Sa'ad-1-9|IAB0636|মোছা. মিম|Mst. Mim|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-4-C|Bed-09
548|Umme Sa'ad-1-10|Meherpur-70|মরিয়ম খাতুন|Moriom Khatun|মেহেরপুর|Meherpur|WAMY-OSP|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-5-A|Bed-01
549|Umme Sa'ad-1-11|EDAS-1356|মোছা. ইয়াসমিন মালিথা|Mst. Yeasmin Malitha|কুষ্টিয়া|Kustia|EDAS|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-5-A|Bed-02
550|Umme Sa'ad-1-12|EDAS-502|নাহিদা আক্তার|Nahida Akter|ময়মনসিংহ|Mymansing|EDAS|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-5-A|Bed-03
551|Umme Sa'ad-1-13|SKB 2448|জিসা মনি|Zisa Moni|কক্সবাজার|Cox's Bazar|SKB|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-5-A|Bed-04
552|Umme Sa'ad-1-14|22100129|সুমাইয়া আক্তার মিম|Sumiya Akter Mim|গাইবান্ধা|Gaibandha|WAMY-OSP|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-5-A|Bed-05
553|Umme Sa'ad-1-15|N/A|মালিহা জান্নাত|Maliya Jannat|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-5-A|Bed-06
554|Umme Sa'ad-1-16|Pabna-52|শেখ জান্নাতুল তাসমিনা|Sheikh Jannatul Tasmina|পাবনা|Pabna|WAMY-OSP|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-5-A|Bed-07
555|Umme Sa'ad-1-17|SKB 2276|সাবিনা ইয়াসমিন|Sabina Yeasmin|কক্সবাজার|Cox's Bazar|SKB|Umme Sa'ad-01|জেবা আতিকা নিশু|Golden-5-A|Bed-08
556|Umme Sa'ad-2-1|ICNA 141|তানিয়া সুলতানা মিম|Tania Sultana Mim|কক্সবাজার|Cox's Bazar|ICNA|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-B|Bed-01
557|Umme Sa'ad-2-2|IAB0464|ফাতেমা তুজ জোহরা|Fatema Tuz Johora|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-B|Bed-02
558|Umme Sa'ad-2-3|N/A|ফাহিমা আক্তার|Fahima Akter|কিশোরগঞ্জ|Kishorganj|WAMY-OSP|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-B|Bed-03
559|Umme Sa'ad-2-4|EDAS- 1707|উম্মে হাবিবা|Umme Habiba|সাতক্ষীরা|Satkhira|EDAS|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-B|Bed-04
560|Umme Sa'ad-2-5|22100134|সুমনা আক্তার|Sumana Akter|গাইবান্ধা|Gaibandha|WAMY-OSP|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-B|Bed-05
561|Umme Sa'ad-2-6|IAB0592|মোছা. আলমিনা খাতুন|Mis. Almina Khatun|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-B|Bed-06
562|Umme Sa'ad-2-7|EDAS-1312|মোছা. লামিশা খাতুন|Mst. Lamisha Khatun|কুষ্টিয়া|Kustia|EDAS|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-B|Bed-07
563|Umme Sa'ad-2-8|EDS-382|মরিয়ম|Moriom|রাজশাহী|Rajshahi|EDS|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-B|Bed-08
564|Umme Sa'ad-2-9|IAB0152|গুলবাহার সিনহা রিমু|Golbahar Sinha Rimu|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-B|Bed-09
565|Umme Sa'ad-2-10|Meherpur-53|জান্নাতুল জেবা|Jannatul Jeba|মেহেরপুর|Meherpur|WAMY-OSP|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-B|Bed-10
566|Umme Sa'ad-2-11|Meherpur-96|আসমানী|Asmani|মেহেরপুর|Meherpur|WAMY-OSP|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-B|Bed-11
567|Umme Sa'ad-2-12|IAB0645|মোছা. সামিরা ইসলাম হুমায়রা|Mst. Samira Islam Humyra|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-B|Bed-12
568|Umme Sa'ad-2-13|Pabna-34|নুসরাত জাহান নূরা|Nusrat Jahan Nura|পাবনা|Pabna|WAMY-OSP|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-B|Bed-13
569|Umme Sa'ad-2-14|N/A|রহিমা খানম|Rahima Khanom|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-C|Bed-01
570|Umme Sa'ad-2-15|N/A|মারিয়াম আক্তার|Mariam Akter|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-C|Bed-02
571|Umme Sa'ad-2-16|Magura-100|মারিয়াম সুলতানা|Mariam Sultana|মাগুরা|Magura|WAMY-OSP|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-C|Bed-03
572|Umme Sa'ad-2-17|ICNA 134|কুলসুম আক্তার|Kulsuma Akter|কক্সবাজার|Cox's Bazar|ICNA|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-C|Bed-04
573|Umme Sa'ad-2-18|N/A|হুমায়রা আক্তার|Humaira Akter|কিশোরগঞ্জ|Kishorganj|WAMY-OSP|Umme Sa'ad-02|আয়েশা সিদ্দিকা|Golden-5-C|Bed-05
574|Umme Sa'ad-3-1|ICNA|মাহি আলম বেবী|Mahi Alam Beby|কক্সবাজার|Cox's Bazar|ICNA|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-5-C|Bed-06
575|Umme Sa'ad-3-2|IAB0701|ইরশাদ জাহান খানম|Ershad Jahan Khanom|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-5-C|Bed-07
576|Umme Sa'ad-3-3|Magura-78|মোছা. মাহি আক্তার দিয়া|Mst. Mahi Akter Dia|মাগুরা|Magura|WAMY-OSP|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-5-C|Bed-08
577|Umme Sa'ad-3-4|ICNA|আজিজা আক্তার|Aziza Akter|কক্সবাজার|Cox's Bazar|ICNA|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-5-C|Bed-09
578|Umme Sa'ad-3-5|22100135|সুমাইয়া আক্তার|Sumaiya Akter|গাইবান্ধা|Gaibandha|WAMY-OSP|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-5-C|Bed-10
579|Umme Sa'ad-3-6|BD-0098|সুমাইয়া খাতুন|Sumaya Khatun|যশোর|Jashore|WAMY-BD|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-6-B|Bed-01
580|Umme Sa'ad-3-7|EDAS-1313|মোছা. রিক্তা খাতুন|Mst. Rikta Khatun|কুষ্টিয়া|Kustia|EDAS|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-6-B|Bed-02
581|Umme Sa'ad-3-8|EDAS-98|রাবেয়া রুহুল কথা|Rabeya Ruhul Kotha|সাতক্ষীরা|Satkhira|EDAS|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-6-B|Bed-03
582|Umme Sa'ad-3-9|IAB0303|রশ্মিয়া তাসনিম সিফা|Rosmiya Tasnim Sifa|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-6-B|Bed-04
583|Umme Sa'ad-3-10|Meherpur-64|খাদিজা আক্তার|Khadiza Akter|মেহেরপুর|Meherpur|WAMY-OSP|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-6-B|Bed-05
584|Umme Sa'ad-3-11|N/A|জ্যোতি খাতুন|Joti Khatun|মেহেরপুর|Meherpur|WAMY-OSP|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-6-B|Bed-06
585|Umme Sa'ad-3-12|BD-0138|তাসলিমা তাহসিন নাঈম|Taslima Tashin Noyme|যশোর|Jashore|WAMY-BD|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-6-B|Bed-07
586|Umme Sa'ad-3-13|EDS-292|সুমাইয়া আক্তার নূর|Sumaiya Akhter Nur|রাজশাহী|Rajshahi|EDS|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-6-B|Bed-08
587|Umme Sa'ad-3-14|IAB0013|আফরিন বর্ণা|Afrin Borna|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-6-B|Bed-09
588|Umme Sa'ad-3-15|N/A|জান্নাতি আক্তার|Jannati Akter|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-6-B|Bed-10
589|Umme Sa'ad-3-16|EDAS-1256|মোহনা|Mohona|নওগাঁ|Naogaon|EDAS|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-6-B|Bed-11
590|Umme Sa'ad-3-17|Narail-100|সুরাইয়া খাতুন|Suraiya Khatun|নড়াইল|Narail|WAMY-OSP|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-6-B|Bed-12
591|Umme Sa'ad-3-18|EDAS-585|হাবিবা খাতুন|Habiba Khatun|লালমনিরহাট|Lalmonirhat|EDAS|Umme Sa'ad-03|নাফিসা তাবাসসুম|Golden-6-B|Bed-13
592|Umme Sa'ad-4-1|N/A|নুসরাত জাহান|Nusrat Jahan|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-A|Bed-01
593|Umme Sa'ad-4-2|IAB0522|মোছা. হালিমাতুস সাদিয়া|Mst. Halimatus Sadia|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-A|Bed-02
594|Umme Sa'ad-4-3|EDAS 1663|উম্মে আতিয়া মোহনা|Umme Atia Mohona|ময়মনসিংহ|Mymansing|EDAS|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-A|Bed-03
595|Umme Sa'ad-4-4|SKB 2264|সুমাইয়া আক্তার|Sumaiya Akter|কক্সবাজার|Cox's Bazar|SKB|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-A|Bed-04
596|Umme Sa'ad-4-5|N/A|লাম ইয়া|Lam Ya|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-A|Bed-05
597|Umme Sa'ad-4-6|N/A|লামিয়া|Lamia|কিশোরগঞ্জ|Kishorganj|WAMY-OSP|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-A|Bed-06
598|Umme Sa'ad-4-7|Magura-75|তাসকিয়া খাতুন|Taskia Khatun|মাগুরা|Magura|WAMY-OSP|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-A|Bed-07
599|Umme Sa'ad-4-8|ICNA 71|উম্মে সালমা|Umme Salma|কক্সবাজার|Cox's Bazar|ICNA|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-A|Bed-08
600|Umme Sa'ad-4-9|IAB0629|মোছা. আয়েশা খাতুন|Mst. Aysha Khatun|সিরাজগঞ্জ (IAB)|IAB-Sirajgonj|IAB|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-A|Bed-09
601|Umme Sa'ad-4-10|Meherpur-69|সুমাইয়া খাতুন|Sumaiya Khatun|মেহেরপুর|Meherpur|WAMY-OSP|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-C|Bed-01
602|Umme Sa'ad-4-11|IAB0077|মায়শা আক্তার মাহি|Maisha Akter Mahi|দিনাজপুর (IAB)|IAB-Dinajpur|IAB|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-C|Bed-02
603|Umme Sa'ad-4-12|22100205|তাহমিনা|Tahmina|কুড়িগ্রাম|Kurigram|WAMY-OSP|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-C|Bed-03
604|Umme Sa'ad-4-13|ICNA 41|আফরিন সুলতানা সামিয়া|Afrin Sultana Samiya|কক্সবাজার|Cox's Bazar|ICNA|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-C|Bed-04
605|Umme Sa'ad-4-14|SKB 2366|তাসফিয়া আলম|Tasfiya Alam|কক্সবাজার|Cox's Bazar|SKB|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-C|Bed-05
606|Umme Sa'ad-4-15|N/A|মিম আক্তার|Mim Akter|বাগেরহাট (IAB)|IAB-Bagerhat|IAB|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-C|Bed-06
607|Umme Sa'ad-4-16|Pabna-24|জান্নাতুল ফেরদৌস|Jannatul Ferdus|পাবনা|Pabna|WAMY-OSP|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-C|Bed-07
608|Umme Sa'ad-4-17|EDS-379|নাফিসা তাবাসসুম|Nafisa Tabassum|রাজশাহী|Rajshahi|EDS|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-C|Bed-08
609|Umme Sa'ad-4-18|EDAS-1215|মোছা. মিফতাহুল জান্নাত|Mst. Miftahul Jannat|নওগাঁ|Naogaon|EDAS|Umme Sa'ad-04|জোবায়দা বিনতে মোহাম্মদ|Golden-6-C|Bed-09
    """

    private const val ROOMS_TSV = """
1|Platinum-1|P1-Down-A|নিচতলা (Down)|7|7|bukhari|আয়াতুল্লাহ নাহিদ|Bukhari-1-1 to Bukhari-1-7
2|Platinum-1|P1-Down-B|নিচতলা (Down)|7|7|bukhari|আয়াতুল্লাহ নাহিদ|Bukhari-1-8 to Bukhari-1-14
3|Platinum-1|P1-Down-C|নিচতলা (Down)|6|6|bukhari|আয়াতুল্লাহ নাহিদ|Bukhari-1-15 to Bukhari-1-20
4|Platinum-1|P1-Down-Common|নিচতলা (Down)|10|10|bukhari|আয়াতুল্লাহ নাহিদ|Bukhari-1-21 to Bukhari-1-30
5|Platinum-1|P1-Up-A|দোতলা (Up)|8|8|bukhari|আয়াতুল্লাহ নাহিদ|Bukhari-2-1 to Bukhari-2-8
6|Platinum-1|P1-Up-B|দোতলা (Up)|8|8|bukhari|আয়াতুল্লাহ নাহিদ|Bukhari-2-9 to Bukhari-2-16
7|Platinum-1|P1-Up-Common|দোতলা (Up)|14|14|bukhari|আয়াতুল্লাহ নাহিদ|Bukhari-2-17 to Bukhari-2-30
8|Platinum-2|P2-Down-A|নিচতলা (Down)|7|7|bukhari|আয়াতুল্লাহ নাহিদ|Bukhari-3-1 to Bukhari-3-7
9|Platinum-2|P2-Down-B|নিচতলা (Down)|7|7|bukhari|আয়াতুল্লাহ নাহিদ|Bukhari-3-8 to Bukhari-3-14
10|Platinum-2|P2-Down-C|নিচতলা (Down)|6|6|bukhari|আয়াতুল্লাহ নাহিদ|Bukhari-3-15 to Bukhari-3-20
11|Platinum-2|P2-Down-Common|নিচতলা (Down)|10|10|bukhari|আয়াতুল্লাহ নাহিদ|Bukhari-3-21 to Bukhari-3-30
12|Platinum-2|P2-Up-A|দোতলা (Up)|8|8|bukhari|আয়াতুল্লাহ নাহিদ|Bukhari-4-1 to Bukhari-4-8
13|Platinum-2|P2-Up-B|দোতলা (Up)|8|8|bukhari|আয়াতুল্লাহ নাহিদ|Bukhari-4-9 to Bukhari-4-16
14|Platinum-2|P2-Up-Common|দোতলা (Up)|14|14|bukhari|আয়াতুল্লাহ নাহিদ|Bukhari-4-17 to Bukhari-4-30
15|Platinum-3|P3-Down-A|নিচতলা (Down)|7|7|shafeyi|সুলতান মাহমুদ|Shafi'ee-1-1 to Shafi'ee-1-7
16|Platinum-3|P3-Down-B|নিচতলা (Down)|7|7|shafeyi|সুলতান মাহমুদ|Shafi'ee-1-8 to Shafi'ee-1-14
17|Platinum-3|P3-Down-C|নিচতলা (Down)|6|6|shafeyi|সুলতান মাহমুদ|Shafi'ee-1-15 to Shafi'ee-1-20
18|Platinum-3|P3-Down-Common|নিচতলা (Down)|11|11|shafeyi|সুলতান মাহমুদ|Shafi'ee-1-21 to Shafi'ee-1-31
19|Platinum-3|P3-Up-A|দোতলা (Up)|8|8|shafeyi|সুলতান মাহমুদ|Shafi'ee-2-1 to Shafi'ee-2-8
20|Platinum-3|P3-Up-B|দোতলা (Up)|9|9|shafeyi|সুলতান মাহমুদ|Shafi'ee-2-9 to Shafi'ee-2-17
21|Platinum-3|P3-Up-Common|দোতলা (Up)|14|14|shafeyi|সুলতান মাহমুদ|Shafi'ee-2-18 to Shafi'ee-2-31
22|Platinum-4|P4-Down-A|নিচতলা (Down)|7|7|shafeyi|সুলতান মাহমুদ|Shafi'ee-3-1 to Shafi'ee-3-7
23|Platinum-4|P4-Down-B|নিচতলা (Down)|7|7|shafeyi|সুলতান মাহমুদ|Shafi'ee-3-8 to Shafi'ee-3-14
24|Platinum-4|P4-Down-C|নিচতলা (Down)|6|6|shafeyi|সুলতান মাহমুদ|Shafi'ee-3-15 to Shafi'ee-3-20
25|Platinum-4|P4-Down-Common|নিচতলা (Down)|11|11|shafeyi|সুলতান মাহমুদ|Shafi'ee-3-21 to Shafi'ee-3-31
26|Platinum-4|P4-Up-A|দোতলা (Up)|8|8|shafeyi|সুলতান মাহমুদ|Shafi'ee-4-1 to Shafi'ee-4-8
27|Platinum-4|P4-Up-B|দোতলা (Up)|8|8|shafeyi|সুলতান মাহমুদ|Shafi'ee-4-9 to Shafi'ee-4-16
28|Platinum-4|P4-Up-Common|দোতলা (Up)|14|14|shafeyi|সুলতান মাহমুদ|Shafi'ee-4-17 to Shafi'ee-4-30
29|Platinum-5|P5-Down-A|নিচতলা (Down)|7|7|salahuddin|নাসির উদ্দিন|Ayubi-1-1 to Ayubi-1-7
30|Platinum-5|P5-Down-B|নিচতলা (Down)|7|7|salahuddin|নাসির উদ্দিন|Ayubi-1-8 to Ayubi-1-14
31|Platinum-5|P5-Down-C|নিচতলা (Down)|6|6|salahuddin|নাসির উদ্দিন|Ayubi-1-15 to Ayubi-1-20
32|Platinum-5|P5-Down-Common|নিচতলা (Down)|11|11|salahuddin|নাসির উদ্দিন|Ayubi-1-21 to Ayubi-1-31
33|Platinum-5|P5-Up-A|দোতলা (Up)|8|8|salahuddin|নাসির উদ্দিন|Ayubi-2-1 to Ayubi-2-8
34|Platinum-5|P5-Up-B|দোতলা (Up)|9|9|salahuddin|নাসির উদ্দিন|Ayubi-2-9 to Ayubi-2-17
35|Platinum-5|P5-Up-Common|দোতলা (Up)|14|14|salahuddin|নাসির উদ্দিন|Ayubi-2-18 to Ayubi-2-31
36|Platinum-6|P6-Down-A|নিচতলা (Down)|7|7|salahuddin|নাসির উদ্দিন|Ayubi-3-1 to Ayubi-3-7
37|Platinum-6|P6-Down-B|নিচতলা (Down)|7|7|salahuddin|নাসির উদ্দিন|Ayubi-3-8 to Ayubi-3-14
38|Platinum-6|P6-Down-C|নিচতলা (Down)|6|6|salahuddin|নাসির উদ্দিন|Ayubi-3-15 to Ayubi-3-20
39|Platinum-6|P6-Down-Common|নিচতলা (Down)|10|10|salahuddin|নাসির উদ্দিন|Ayubi-3-21 to Ayubi-3-30
40|Platinum-6|P6-Up-A|দোতলা (Up)|8|8|salahuddin|নাসির উদ্দিন|Ayubi-4-1 to Ayubi-4-8
41|Platinum-6|P6-Up-B|দোতলা (Up)|8|8|salahuddin|নাসির উদ্দিন|Ayubi-4-9 to Ayubi-4-16
42|Platinum-6|P6-Up-Common|দোতলা (Up)|14|14|salahuddin|নাসির উদ্দিন|Ayubi-4-17 to Ayubi-4-30
43|Golden-2|Golden-2-A|১ম তলা|10|10|umm_ayman|তাহমিনা বিনতে নূর|Umme Ayman-1-1 to Umme Ayman-1-10
44|Golden-2|Golden-2-B|১ম তলা|12|12|umm_ayman|তাহমিনা বিনতে নূর|Umme Ayman-1-11 to Umme Ayman-1-22
45|Golden-2|Golden-2-C|১ম তলা|11|11|umm_ayman|তাহমিনা বিনতে নূর|Umme Ayman-2-1 to Umme Ayman-2-11
46|Golden-3|Golden-3-A|১ম তলা|11|11|umm_ayman|তাহমিনা বিনতে নূর|Umme Ayman-2-12 to Umme Ayman-2-22
47|Golden-3|Golden-3-B|১ম তলা|12|12|umm_ayman|তাহমিনা বিনতে নূর|Umme Ayman-3-1 to Umme Ayman-3-12
48|Golden-3|Golden-3-C|১ম তলা|10|10|umm_ayman|তাহমিনা বিনতে নূর|Umme Ayman-3-13 to Umme Ayman-3-22
49|Golden-4|Golden-4-A|১ম তলা|10|10|umm_ayman|তাহমিনা বিনতে নূর|Umme Ayman-4-1 to Umme Ayman-4-10
50|Golden-4|Golden-4-B|১ম তলা|13|13|umm_ayman|তাহমিনা বিনতে নূর|Umme Ayman-4-11 to Umme Ayman-4-23
51|Golden-4|Golden-4-C|১ম তলা|9|9|umm_saad|হামিদা আক্তার|Umme Sa'ad-1-1 to Umme Sa'ad-1-9
52|Golden-5|Golden-5-A|১ম তলা|8|8|umm_saad|হামিদা আক্তার|Umme Sa'ad-1-10 to Umme Sa'ad-1-17
53|Golden-5|Golden-5-B|১ম তলা|13|13|umm_saad|হামিদা আক্তার|Umme Sa'ad-2-1 to Umme Sa'ad-2-13
54|Golden-5|Golden-5-C|১ম তলা|10|10|umm_saad|হামিদা আক্তার|Umme Sa'ad-2-14 to Umme Sa'ad-2-18 + Umme Sa'ad-3-1 to Umme Sa'ad-3-5
55|Golden-6|Golden-6-B|১ম তলা|13|13|umm_saad|হামিদা আক্তার|Umme Sa'ad-3-6 to Umme Sa'ad-3-18
56|Golden-6|Golden-6-A|১ম তলা|9|9|umm_saad|হামিদা আক্তার|Umme Sa'ad-4-1 to Umme Sa'ad-4-9
57|Golden-6|Golden-6-C|১ম তলা|9|9|umm_saad|হামিদা আক্তার|Umme Sa'ad-4-10 to Umme Sa'ad-4-18
58|Golden-1 (Staff)|Golden-1-Staff|১ম তলা|6|6|bukhari|ক্যাম্প ব্যবস্থাপনা টিম|Staff
59|Golden-7 (Guest)|Golden-7-A|১ম তলা|6|6|bukhari|ক্যাম্প ব্যবস্থাপনা টিম|Guest
60|Golden-7 (Guest)|Golden-7-B|১ম তলা|6|6|bukhari|ক্যাম্প ব্যবস্থাপনা টিম|
61|Golden-7 (Guest)|Golden-7-C|১ম তলা|6|6|bukhari|ক্যাম্প ব্যবস্থাপনা টিম|
62|Rajonigandha / Silver|Rajo-101|১ম তলা|8|8|sumaiya|শারমিন সুলতানা|Sumaiya-1-1 to Sumaiya-1-8
63|Rajonigandha / Silver|Rajo-102|১ম তলা|8|8|sumaiya|শারমিন সুলতানা|Sumaiya-1-9 to Sumaiya-1-16
64|Rajonigandha / Silver|Rajo-103|১ম তলা|8|8|sumaiya|শারমিন সুলতানা|Sumaiya-1-17 to Sumaiya-1-21 + Sumaiya-2-1 to Sumaiya-2-3
65|Rajonigandha / Silver|Rajo-104|১ম তলা|9|9|sumaiya|শারমিন সুলতানা|Sumaiya-2-4 to Sumaiya-2-12
66|Rajonigandha / Silver|Rajo-105|১ম তলা|9|9|sumaiya|শারমিন সুলতানা|Sumaiya-2-13 to Sumaiya-2-21
67|Rajonigandha / Silver|Rajo-106|১ম তলা|8|8|sumaiya|শারমিন সুলতানা|Sumaiya-3-1 to Sumaiya-3-8
68|Rajonigandha / Silver|Rajo-Common|১ম তলা|13|13|sumaiya|শারমিন সুলতানা|Sumaiya-3-9 to Sumaiya-3-21
69|Rajonigandha / Silver|Rajo-202|২য় তলা|7|7|sumaiya|শারমিন সুলতানা|Sumaiya-4-1 to Sumaiya-4-7
70|Rajonigandha / Silver|Rajo-203|২য় তলা|7|7|sumaiya|শারমিন সুলতানা|Sumaiya-4-8 to Sumaiya-4-14
71|Rajonigandha / Silver|Rajo-204|২য় তলা|7|7|sumaiya|শারমিন সুলতানা|Sumaiya-4-15 to Sumaiya-4-21
    """

    private data class GroupDefaults(
        val groupId: String,
        val groupNameBn: String,
        val groupNameEn: String,
        val gender: String,
        val age: Int,
        val classLevel: String,
        val leaderPhone: String
    )

    private val groupDefaults = listOf(
        GroupDefaults("bukhari", "বুখারী গ্রুপ", "Bukhari Group", "Male", 10, "৪র্থ শ্রেণি", "01711-260101") to BUKHARI_TSV,
        GroupDefaults("shafeyi", "শাফে’য়ী গ্রুপ", "Shafi'ee Group", "Male", 11, "৪র্থ ও ৫ম শ্রেণি", "01711-260102") to SHAFEYI_TSV,
        GroupDefaults("salahuddin", "সালাহউদ্দিন আয়ুবী গ্রুপ", "Salahuddin Ayubi Group", "Male", 13, "৬ষ্ঠ ও ৭ম শ্রেণি", "01711-260103") to SALAHUDDIN_TSV,
        GroupDefaults("sumaiya", "সুমাইয়া গ্রুপ", "Sumaiya Group", "Female", 10, "৪র্থ শ্রেণি", "01819-260104") to SUMAIYA_TSV,
        GroupDefaults("umm_ayman", "উম্মে আইমান গ্রুপ", "Umme Ayman Group", "Female", 11, "৪র্থ ও ৫ম শ্রেণি", "01819-260105") to UMM_AYMAN_TSV,
        GroupDefaults("umm_saad", "উম্মে সা’দ (হাম্বলী) গ্রুপ", "Umme Sa'ad Group", "Female", 13, "৬ষ্ঠ ও ৭ম শ্রেণি", "01819-260106") to UMM_SAAD_TSV
    )

    fun buildOfficialParticipants(): List<ParticipantEntity> {
        val result = ArrayList<ParticipantEntity>(615)
        val baseCheckIn = 1792634400000L // 22 Oct 2026 morning
        for ((meta, tsv) in groupDefaults) {
            for (rawLine in tsv.lineSequence()) {
                val line = rawLine.trim()
                if (line.isEmpty()) continue
                val cols = line.split('|')
                if (cols.size < 12) continue
                val id = cols[0].toLong()
                val idx = id.toInt()
                val inChargeBn = cols[9]
                result.add(
                    ParticipantEntity(
                        id = id,
                        campCode = cols[1],
                        fileNo = cols[2],
                        nameBn = cols[3],
                        nameEn = cols[4],
                        districtBn = cols[5],
                        districtEn = cols[6],
                        gender = meta.gender,
                        age = meta.age,
                        groupId = meta.groupId,
                        groupNameBn = meta.groupNameBn,
                        groupNameEn = meta.groupNameEn,
                        subgroup = cols[8],
                        roomNo = cols[10],
                        bedNo = cols[11],
                        mealPref = "নিয়মিত",
                        guardianName = "সাব-গ্রুপ ইনচার্জ: $inChargeBn",
                        guardianPhone = meta.leaderPhone,
                        isCheckedIn = true,
                        checkInTime = baseCheckIn + (id * 15000L),
                        medicalFlag = null,
                        totalScore = 0.0,
                        attendancePercentage = 100.0f,
                        photoAvatarSeed = (idx % 12) + 1,
                        classLevel = meta.classLevel,
                        organization = cols[7],
                        completedStationSeals = ""
                    )
                )
            }
        }
        return result
    }

    fun buildOfficialRooms(): List<RoomEntity> {
        val rooms = ArrayList<RoomEntity>(72)
        for (rawLine in ROOMS_TSV.lineSequence()) {
            val line = rawLine.trim()
            if (line.isEmpty()) continue
            val cols = line.split('|')
            if (cols.size < 9) continue
            rooms.add(
                RoomEntity(
                    id = cols[0].toLong(),
                    buildingName = cols[1],
                    roomNo = cols[2],
                    floor = cols[3],
                    capacity = cols[4].toIntOrNull() ?: 8,
                    assignedCount = cols[5].toIntOrNull() ?: 0,
                    assignedGroupId = cols[6],
                    inChargeName = cols[7],
                    inChargePhone = "01711-260100",
                    notes = cols[8]
                )
            )
        }
        return rooms
    }
}
