#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script to generate a complete Microsoft Word (.docx) file containing 
comprehensive documentation for the Residential Life Skills Camp Companion Android App.
"""

import zipfile
import os
import xml.sax.saxutils as saxutils

def escape_xml(text):
    return saxutils.escape(text)

def create_p(text, style="Normal", bold=False, size=24, color="000000", space_after=120):
    b_tag = "<w:b/>" if bold else ""
    color_tag = f'<w:color w:val="{color}"/>' if color else ""
    return f"""
    <w:p>
        <w:pPr>
            <w:pStyle w:val="{style}"/>
            <w:spacing w:after="{space_after}"/>
        </w:pPr>
        <w:r>
            <w:rPr>
                {b_tag}
                <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri" w:cs="Kalpurush"/>
                {color_tag}
                <w:sz w:val="{size}"/>
            </w:rPr>
            <w:t xml:space="preserve">{escape_xml(text)}</w:t>
        </w:r>
    </w:p>"""

def create_bullet(text, level=0):
    return f"""
    <w:p>
        <w:pPr>
            <w:pStyle w:val="ListParagraph"/>
            <w:numPr>
                <w:ilvl w:val="{level}"/>
                <w:numId w:val="1"/>
            </w:numPr>
            <w:spacing w:after="80"/>
        </w:pPr>
        <w:r>
            <w:rPr>
                <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri" w:cs="Kalpurush"/>
                <w:sz w:val="22"/>
            </w:rPr>
            <w:t xml:space="preserve">{escape_xml(text)}</w:t>
        </w:r>
    </w:p>"""

def build_docx(filename):
    content_types = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
    <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
    <Default Extension="xml" ContentType="application/xml"/>
    <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
    <Override PartName="/word/numbering.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.numbering+xml"/>
</Types>"""

    rels = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
    <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>"""

    word_rels = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
    <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/numbering" Target="numbering.xml"/>
</Relationships>"""

    numbering = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:numbering xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
    <w:abstractNum w:abstractNumId="0">
        <w:multiLevelType w:val="hybridMultilevel"/>
        <w:lvl w:ilvl="0">
            <w:start w:val="1"/>
            <w:numFmt w:val="bullet"/>
            <w:lvlText w:val="•"/>
            <w:lvlJc w:val="left"/>
            <w:pPr><w:ind w:left="720" w:hanging="360"/></w:pPr>
        </w:lvl>
    </w:abstractNum>
    <w:num w:numId="1">
        <w:abstractNumId w:val="0"/>
    </w:num>
</w:numbering>"""

    body_elements = []

    # Title
    body_elements.append(create_p("আবাসিক জীবন দক্ষতা ক্যাম্প (LSC) ক্যাম্প বন্ধু অ্যাপ", bold=True, size=40, color="0F5132", space_after=140))
    body_elements.append(create_p("পূর্ণাঙ্গ টেকনিক্যাল ইউজার ম্যানুয়াল ও কার্যপদ্ধতি নির্দেশিকা (Comprehensive Documentation)", bold=True, size=24, color="1E3A8A", space_after=300))

    # Meta
    body_elements.append(create_p("অ্যাপ সংস্করণ: ৩.০ (অফলাইন-ফার্স্ট আর্কিটেকচার | জেপ্যাক কম্পোজ | রুম ডাটাবেজ)", bold=False, size=20, color="475569", space_after=200))
    body_elements.append(create_p("প্রস্তুতকারী: ডেভেলপমেন্ট অ্যান্ড টেকনিক্যাল কোঅর্ডিনেশন টিম", bold=False, size=20, color="475569", space_after=400))

    # Section 1
    body_elements.append(create_p("১. ভূমিকা ও মূল উদ্দেশ্য (Introduction & Core Purpose)", bold=True, size=28, color="0F5132", space_after=160))
    body_elements.append(create_p("আবাসিক জীবন দক্ষতা ক্যাম্প (Residential Life Skills Camp - LSC) একটি পূর্ণাঙ্গ ৫ দিনব্যাপী প্রশিক্ষণ ও ক্যারেক্টার বিল্ডিং ক্যাম্প। এই ক্যাম্পে ৬০৩ জন প্রশিক্ষণার্থী, ৬টি প্রধান দল, ৬টি শিক্ষণ স্টেশন, ৩৯ জন ক্যাম্প কর্মকর্তা ও ভলান্টিয়ার অংশগ্রহণ করেন। মাঠপর্যায়ে কোনো ইন্টারনেট সংযোগ না থাকলেও যেন সম্পূর্ণ ক্যাম্পের যাবতীয় কার্যক্রম নিশ্ছিদ্রভাবে পরিচালিত হতে পারে, সে উদ্দেশ্যে এই অ্যান্ড্রয়েড অ্যাপ্লিকেশনটি তৈরি করা হয়েছে।", size=22, space_after=200))
    
    # Section 2
    body_elements.append(create_p("২. সিস্টেম আর্কিটেকচার ও ডেটাবেজ কাঠামো (Technical Architecture)", bold=True, size=28, color="0F5132", space_after=160))
    body_elements.append(create_bullet("প্রোগ্রামিং ল্যাঙ্গুয়েজ: ১০০% আধুনিক Kotlin (কোটলিন)"))
    body_elements.append(create_bullet("ইউজার ইন্টারফেস: Jetpack Compose ও Material Design 3 (M3)"))
    body_elements.append(create_bullet("আর্কিটেকচার প্যাটার্ন: MVVM (Model-View-ViewModel) ও Clean Architecture"))
    body_elements.append(create_bullet("লোকাল ডেটাবেজ: Android Room Database (১৫টি এনটিটি ও অপ্টিমাইজড কুয়েরি)"))
    body_elements.append(create_bullet("কনকারেন্সি ও রিঅ্যাক্টিভ ডেটা: Kotlin Coroutines এবং StateFlow / Flow"))
    body_elements.append(create_bullet("অফলাইন সুবিধা: সম্পূর্ণ ডেটা ডিভাইসের SQLite স্টোরেজে সংরক্ষিত থাকে; ইন্টারনেট ছাড়াই শতভাগ সক্রিয়"))

    # Section 3
    body_elements.append(create_p("৩. লগইন পাসওয়ার্ড ও সিকিউরিটি ব্যবস্থাপনা (Admin Security & Passcode)", bold=True, size=28, color="0F5132", space_after=160))
    body_elements.append(create_p("ক্যাম্পের সংবেদনশীল কনফিগারেশন যেমন সময়সূচি, সেশন, দলনেতা, মেন্টর, বিচারক তালিকা বা এক্সেল ইমপোর্ট যাতে অননুমোদিত কেউ পরিবর্তন করতে না পারে, সেজন্য একটি কেন্দ্রীয় এডমিন সিকিউরিটি গেটওয়ে রয়েছে।", size=22, space_after=160))
    body_elements.append(create_bullet("ডিফল্ট পাসওয়ার্ড: admin123 (বিকল্প ব্যাকআপ কোড: camp2026)"))
    body_elements.append(create_bullet("টপ বারে সিকিউরিটি ইন্ডিকেটর: লক অবস্থায় ধূসর লক আইকন এবং আনলক অবস্থায় উজ্জ্বল সবুজ 'এডমিন' ব্যাজ দেখা যায়"))
    body_elements.append(create_bullet("সুরক্ষিত অ্যাকশন: সেশন এডিট, গ্রুপ লিডার/মেন্টর পরিবর্তন, বিচারক এডিট/ডিলিট, সরাসরি বুলেটিন প্রকাশ এবং এক্সেল বাল্ক আপলোড"))

    # Section 4
    body_elements.append(create_p("৪. সার্বজনীন কিউআর কোড স্ক্যানার (Universal QR Scanner)", bold=True, size=28, color="0F5132", space_after=160))
    body_elements.append(create_p("ক্যাম্পের ৬০৩ জন অংশগ্রহণকারীর প্রত্যেকের একটি নির্দিষ্ট ক্যাম্প কোড (যেমন: LSC-2026-001) রয়েছে। টপ বারের কিউআর স্ক্যানার বাটনে চাপ দিলে একটি স্মার্ট ক্যামেরা স্ক্যানার চালু হয়।", size=22, space_after=160))
    body_elements.append(create_bullet("সাধারণ অনুসন্ধান মোড (General Lookup): স্ক্যান করার সাথে সাথে প্রশিক্ষণার্থীর সম্পূর্ণ প্রোফাইল, রক্তের গ্রুপ, জরুরি ফোন ও অভিভাবকের তথ্য দৃশ্যমান হয়"))
    body_elements.append(create_bullet("হাজিরা মোড (Attendance Check-in): জামায়াত বা সেশনের উপস্থিতির জন্য স্ক্যান করলেই তাৎক্ষণিক টিক মার্ক ও প্রেজেন্ট কাউন্ট যুক্ত হয়"))
    body_elements.append(create_bullet("খাবার বণ্টন মোড (Meal Claiming): সকাল, দুপুর বা রাতের খাবার নেওয়ার সময় স্ক্যান করলে নিশ্চিত হয় যে খাবার গ্রহণ হয়েছে এবং ডাবল ক্লেইম রোধ করে"))
    body_elements.append(create_bullet("ম্যানুয়াল কোড ইনপুট: ক্যামেরা অনুপলব্ধ থাকলে কোড টাইপ করেও স্ক্যান সম্পন্ন করা যায়"))

    # Section 5
    body_elements.append(create_p("৫. অংশগ্রহণকারী তালিকা, ডিজিটাল আইডি কার্ড ও সার্টিফিকেট", bold=True, size=28, color="0F5132", space_after=160))
    body_elements.append(create_bullet("৬০৩ জনের ডাটা ফিল্টারিং: নাম, রোল নম্বর, জেলা ও ৬টি দলের ভিত্তিতে ইনস্ট্যান্ট সার্চ"))
    body_elements.append(create_bullet("ডিজিটাল আইডি কার্ড: অফিশিয়াল গোল্ড-এমারেল্ড ব্যাজ, ফটো প্রিভিউ, কিউআর কোড ও মেডিকেল সতর্কতা সমন্বিত স্মার্ট কার্ড"))
    body_elements.append(create_bullet("অটোমেটিক সমাপ্তি সার্টিফিকেট: ক্যাম্প সফলভাবে সম্পন্ন করার পর ক্যাম্প চিফ স্বাক্ষরিত ডিজিটাল সার্টিফিকেট প্রস্তুত ও প্রিভিউ"))

    # Section 6
    body_elements.append(create_p("৬. স্টেশন রোটেশন ও সময়সূচি এডিটিং (Stations & Schedule)", bold=True, size=28, color="0F5132", space_after=160))
    body_elements.append(create_p("ক্যাম্পে ৬টি বিশেষায়িত জীবন দক্ষতা স্টেশন রয়েছে: ১. কুরআন ও তাজবীদ ২. আত্মরক্ষা ও ফিটনেস ৩. ফার্স্ট এইড ও উদ্ধার ৪. টিমওয়ার্ক ও কমিউনিকেশন ৫. প্রব্লেম সলভিং ৬. মোরাল লিডারশিপ।", size=22, space_after=160))
    body_elements.append(create_bullet("দিন ১ থেকে দিন ৫ এর টাইমলাইন ভিউ: প্রতিটি সেশনের শুরুর ও সমাপ্তির সময়, ভেন্যু এবং বিবরণ"))
    body_elements.append(create_bullet("গ্রুপ স্টেশন রোটেশন ম্যাট্রিক্স: কোন সময়ে কোন গ্রুপ কোন স্টেশনে থাকবে তা স্বয়ংক্রিয়ভাবে প্রদর্শিত হয়"))
    body_elements.append(create_bullet("সরাসরি সেশন এডিট: এডমিন পাসওয়ার্ড দিয়ে আনলক করে যেকোনো সেশনের সময়সূচি, শিরোনাম ও ভেন্যু তাৎক্ষণিক পরিবর্তন করা যায়"))
    body_elements.append(create_bullet("গ্রুপ লিডার ও মেন্টর এডিট: প্রতিটি দলের দলনেতা, মোবাইল এবং সাব-গ্রুপ ভিত্তিক মেন্টরদের দায়িত্ব আপডেট করার ব্যবস্থা"))

    # Section 7
    body_elements.append(create_p("৭. প্রতিযোগিতা, স্কোরবোর্ড ও বিচারকমণ্ডলী প্যানেল", bold=True, size=28, color="0F5132", space_after=160))
    body_elements.append(create_bullet("ইভেন্ট তালিকা: ক্বিরাত, হাদিস মুখস্থ, ইসলামী জ্ঞান কুইজ, ক্যালিগ্রাফি, বিতর্ক ও ক্রীড়া প্রতিযোগিতা"))
    body_elements.append(create_bullet("বিচারক স্কোর এন্ট্রি: বিশুদ্ধতা (৪০), উপস্থাপনা (৩০) এবং শৃঙ্খলা (৩০) এর উপর ভিত্তি করে ১০০ নম্বরের স্মার্ট স্লাইডার মূল্যায়ন"))
    body_elements.append(create_bullet("গ্রুপ লিডারবোর্ড: সকল প্রতিযোগিতার প্রাপ্ত পয়েন্ট স্বয়ংক্রিয়ভাবে যোগ হয়ে দলের র‌্যাঙ্কিং ও পয়েন্ট প্রগ্রেস বার আপডেট হয়"))
    body_elements.append(create_bullet("বিচারকমণ্ডলী ব্যবস্থাপনা: বিচারকদের তালিকা, পদবী, ইভেন্ট ক্যাটাগরি, যোগাযোগ নম্বর এডিট ও ডিলিট করার পূর্ণ সুবিধা"))

    # Section 8
    body_elements.append(create_p("৮. দেয়াল পত্রিকা ও সরাসরি লাইভ বুলেটিন প্রকাশ (Wall Magazine & Bulletin)", bold=True, size=28, color="0F5132", space_after=160))
    body_elements.append(create_p("ক্যাম্পের প্রতিদিনের দেয়াল পত্রিকা সাবমিশন এবং তাৎক্ষণিক জরুরি নোটিশ প্রকাশের জন্য এই মডিউলটি ব্যবহৃত হয়।", size=22, space_after=160))
    body_elements.append(create_bullet("সরাসরি পাবলিশিং বাটন: '+ সরাসরি নতুন বুলেটিন প্রকাশ করুন' এ ক্লিক করে ডায়ালগ ওপেন করা যায়"))
    body_elements.append(create_bullet("ক্যাটাগরি ট্যাগ: জরুরি নোটিশ, দৈনিক বুলেটিন, প্রতিযোগিতার ফলাফল, ডাইনিং আপডেট ও স্টেশন হাইলাইট"))
    body_elements.append(create_bullet("স্পটলাইট ফিচার: দিনের সেরা গ্রুপ বা সেরা কৃতিত্ব অর্জনকারী দলের নাম হাইলাইট করা যায়"))
    body_elements.append(create_bullet("রিয়েল-টাইম লাইভ ডিসপ্লে: প্রকাশ করার সাথে সাথেই ফিডের শীর্ষে নোটিশ প্রদর্শিত হয়"))

    # Section 9
    body_elements.append(create_p("৯. এক্সেল ও স্প্রেডশীট বাল্ক ইমপোর্ট সিস্টেম (Excel / CSV Batch Upload)", bold=True, size=28, color="0F5132", space_after=160))
    body_elements.append(create_p("একসাথে বহু সংখ্যক বিচারক, ভলান্টিয়ার বা দায়িত্বপ্রাপ্ত কর্মকর্তা যুক্ত করার জন্য তৈরি করা হয়েছে ইউনিভার্সাল বাল্ক ইমপোর্টার।", size=22, space_after=160))
    body_elements.append(create_bullet("ফাইল সিলেক্টর: মোবাইল বা কম্পিউটার স্টোরেজ থেকে সরাসরি .csv ফাইল সিলেক্ট করা যায়"))
    body_elements.append(create_bullet("ডিরেক্ট কপি-পেস্ট: মাইক্রোসফট এক্সেল বা গুগল শিট থেকে সারি কপি করে সরাসরি টেক্সট বক্সে পেস্ট করা যায়"))
    body_elements.append(create_bullet("টার্গেট নির্বাচন: বিচারকমণ্ডলী (Judges), ভলান্টিয়ার (Volunteers) এবং দায়িত্বপ্রাপ্ত কর্মকর্তা (Duty Officers)"))
    body_elements.append(create_bullet("নমুনা টেমপ্লেট: বাটন চাপলেই সঠিক কলাম ফরম্যাট স্ক্রিনে চলে আসে, যা নতুনদের জন্য ব্যবহার অত্যন্ত সহজ করে তোলে"))

    # Section 10
    body_elements.append(create_p("১০. মেডিকেল, ইনভেন্টরি ও জরুরি সেফগার্ডিং", bold=True, size=28, color="0F5132", space_after=160))
    body_elements.append(create_bullet("মেডিকেল রেকর্ড: অংশগ্রহণকারীদের প্রেসার, পালস, তাপমাত্রা, ডায়াগনোসিস এবং জরুরি অ্যাম্বুলেন্স ফ্ল্যাগ"))
    body_elements.append(create_bullet("পুরস্কার ও কিট ইনভেন্টরি: ট্রফি, মেডেল, ক্রেস্ট, বুক গিফট ও স্টেশনারি সামগ্রীর উদ্বৃত্ত স্টক ট্র্যাকিং এবং বিতরণ ভাউচার"))
    body_elements.append(create_bullet("জরুরি সেফগার্ডিং: ইনসিডেন্ট রিপোর্টিং, ক্যাম্প সিকিউরিটি কন্ট্রোল ও সরাসরি জরুরি হেল্পলাইন ডায়ালিং"))

    # Summary
    body_elements.append(create_p("১১. উপসংহার (Conclusion)", bold=True, size=28, color="0F5132", space_after=160))
    body_elements.append(create_p("এই অ্যাপ্লিকেশনটি আবাসিক জীবন দক্ষতা ক্যাম্পের প্রশাসনিক শৃঙ্খলা, সময়ানুবর্তিতা এবং ডিজিটাল ব্যবস্থাপনায় এক বৈপ্লবিক মাত্রা যোগ করেছে। ইন্টারনেটের সীমাবদ্ধতা সত্ত্বেও ডেটাবেজের সুরক্ষা ও অফলাইন কর্মক্ষমতা ক্যাম্পের প্রতিটি কার্যক্রমকে করেছে গতিশীল ও নির্ভুল।", size=22, space_after=200))

    doc_xml = f"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
    <w:body>
        {''.join(body_elements)}
        <w:sectPr>
            <w:pgSz w:w="12240" w:h="15840"/>
            <w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440" w:header="720" w:footer="720" w:gutter="0"/>
        </w:sectPr>
    </w:body>
</w:document>"""

    with zipfile.ZipFile(filename, 'w', zipfile.ZIP_DEFLATED) as docx:
        docx.writestr('[Content_Types].xml', content_types)
        docx.writestr('_rels/.rels', rels)
        docx.writestr('word/_rels/document.xml.rels', word_rels)
        docx.writestr('word/document.xml', doc_xml)
        docx.writestr('word/numbering.xml', numbering)

    print(f"Generated {filename} successfully ({os.path.getsize(filename)} bytes)")

if __name__ == '__main__':
    build_docx('/app/LSC_Camp_Companion_Documentation.docx')
    build_docx('/LSC_Camp_Companion_Documentation.docx')
