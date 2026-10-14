#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script to generate an exhaustive, professional Microsoft Word (.docx) manual
for Residential Life Skills Camp (LSC 2025/2026) Android App.
Incorporates all sections from LSC 2025 Rationale Bangla.pdf:
- আবাসন ও কক্ষ বরাদ্দ
- দল ও সাবগ্রুপ ব্যবস্থাপনা
- দায়িত্ব বণ্টন ও ডিউটি রোস্টার
- ভ্রমণ ও যানবাহন
- ৫ দিনের কর্মসূচি ও দৈনিক কার্যক্রমের ব্যতিক্রম
- উপহার ও কিট বিতরণ
- ফলাফল মূল্যায়ন ও সনদ
- কিউআর কোড হাজিরা ও খাবার বিতরণ
- এডমিন সিকিউরিটি ও এক্সেল বাল্ক আপলোড
"""

import zipfile
import os
import xml.sax.saxutils as saxutils

def escape_xml(text):
    return saxutils.escape(str(text))

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

def create_table_row(cells, is_header=False):
    tc_list = []
    for cell in cells:
        shd = '<w:shd w:val="clear" w:color="auto" w:fill="0F5132"/>' if is_header else '<w:shd w:val="clear" w:color="auto" w:fill="F8FAFC"/>'
        txt_color = "FFFFFF" if is_header else "1E293B"
        bold_tag = "<w:b/>" if is_header else ""
        tc_xml = f"""
        <w:tc>
            <w:tcPr>
                <w:tcW w:w="2400" w:type="dxa"/>
                {shd}
                <w:tcMar>
                    <w:top w:w="120" w:type="dxa"/>
                    <w:left w:w="140" w:type="dxa"/>
                    <w:bottom w:w="120" w:type="dxa"/>
                    <w:right w:w="140" w:type="dxa"/>
                </w:tcMar>
            </w:tcPr>
            <w:p>
                <w:pPr>
                    <w:spacing w:after="0"/>
                </w:pPr>
                <w:r>
                    <w:rPr>
                        {bold_tag}
                        <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri" w:cs="Kalpurush"/>
                        <w:color w:val="{txt_color}"/>
                        <w:sz w:val="20"/>
                    </w:rPr>
                    <w:t xml:space="preserve">{escape_xml(cell)}</w:t>
                </w:r>
            </w:p>
        </w:tc>"""
        tc_list.append(tc_xml)
    return f"<w:tr>{''.join(tc_list)}</w:tr>"

def create_table(headers, rows):
    r_xml = [create_table_row(headers, is_header=True)]
    for row in rows:
        r_xml.append(create_table_row(row, is_header=False))
    return f"""
    <w:tbl>
        <w:tblPr>
            <w:tblW w:w="9600" w:type="dxa"/>
            <w:tblBorders>
                <w:top w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/>
                <w:left w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/>
                <w:bottom w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/>
                <w:right w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/>
                <w:insideH w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/>
                <w:insideV w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/>
            </w:tblBorders>
            <w:tblCellMar>
                <w:top w:w="80" w:type="dxa"/>
                <w:left w:w="100" w:type="dxa"/>
                <w:bottom w:w="80" w:type="dxa"/>
                <w:right w:w="100" w:type="dxa"/>
            </w:tblCellMar>
        </w:tblPr>
        {''.join(r_xml)}
    </w:tbl>
    <w:p><w:pPr><w:spacing w:after="160"/></w:pPr></w:p>"""

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

    # Title & Header
    body_elements.append(create_p("আবাসিক জীবন দক্ষতা ক্যাম্প (LSC) ক্যাম্প সঙ্গী অ্যাপ", bold=True, size=40, color="0F5132", space_after=140))
    body_elements.append(create_p("পূর্ণাঙ্গ টেকনিক্যাল ও অপারেশনাল ম্যানুয়াল (Official Operational Manual & System Architecture)", bold=True, size=24, color="1E3A8A", space_after=280))
    body_elements.append(create_p("পর্যালোচিত উৎস: LSC 2025 Rationale Bangla (পৃষ্ঠা ৮–৩৩, ৩৫–৬২)", bold=False, size=20, color="64748B", space_after=100))
    body_elements.append(create_p("অ্যাপ সংস্করণ: ৩.৫ | আর্কিটেকচার: অফলাইন-ফার্স্ট, অ্যান্ড্রয়েড রুম, জেটপ্যাক কম্পোজ, মেটেরিয়াল ৩", bold=False, size=20, color="64748B", space_after=320))

    # ১. পরিচিতি, লক্ষ্য ও পরিধি
    body_elements.append(create_p("১. পরিচিতি, লক্ষ্য ও পরিধি (Introduction, Objectives & Scope)", bold=True, size=28, color="0F5132", space_after=140))
    body_elements.append(create_p("আবাসিক জীবন দক্ষতা ক্যাম্প (Residential Life Skills Camp - LSC) হল কিশোর ও তরুণদের আত্মউন্নয়ন, নৈতিক নেতৃত্ব, নিয়মানুবর্তিতা এবং ব্যবহারিক দক্ষতা বিকাশের জন্য একটি নিবিড় আবাসিক প্রশিক্ষণ কর্মসূচি। এই ক্যাম্পে দেশের বিভিন্ন জেলা থেকে আগত ৬০৩ জন প্রশিক্ষণার্থী, ৬টি মূল দল (বুখারী, মুসলিম, আবু দাউদ, তিরমিযী, নাসাঈ, ইবনে মাজাহ/সালাহউদ্দীন/সুমাইয়া), ৬টি বিশেষায়িত লার্নিং স্টেশন, এবং ৩৯ জন কর্মকর্তা ও ভলান্টিয়ার অংশ নেন।", size=22, space_after=140))
    body_elements.append(create_bullet("মূল লক্ষ্য: কোনো নিরবচ্ছিন্ন ইন্টারনেট সংযোগ ছাড়াই ক্যাম্পের উপস্থিতি, আহার, রোটেশন, আবাসন, চিকিৎসা, কুইজ ও প্রতিযোগিতা ব্যবস্থাপনা শতভাগ ডিজিটাল ও অফলাইনে সম্পন্ন করা।"))
    body_elements.append(create_bullet("ব্যবহারকারী গ্রুপ: ক্যাম্প চিফ, স্টেশন ইনচার্জ, লজিস্টিকস ও ফুড অফিসার, মেডিকেল অফিসার, গ্রুপ মেন্টর এবং ভলান্টিয়ার।"))
    body_elements.append(create_bullet("সীমাবদ্ধতা ও সমাধান: প্রত্যন্ত অঞ্চলে ওয়াইফাই বা মোবাইল নেটওয়ার্ক বিভ্রাট থাকলে অ্যাপটি SQLite Room লোকাল ডেটাবেজে কাজ করে এবং পরবর্তীতে স্বয়ংক্রিয় সিঙ্ক সুবিধা দেয়।"))

    # ২. ক্যাম্প সেটআপ ও ড্যাশবোর্ড
    body_elements.append(create_p("২. ক্যাম্প সেটআপ ও কেন্দ্রীয় ড্যাশবোর্ড (Camp Setup & Master Dashboard)", bold=True, size=28, color="0F5132", space_after=140))
    body_elements.append(create_p("অ্যাপ্লিকেশনের হোম স্ক্রিনেই রয়েছে একটি রিয়েল-টাইম ইন্টেলিজেন্ট ড্যাশবোর্ড। এখান থেকে ক্যাম্পের মূল স্ট্যাটিস্টিকস মুহূর্তের মধ্যে অবলোকন করা যায়।", size=22, space_after=140))
    body_elements.append(create_bullet("ক্যাম্পের মৌলিক প্যারামিটার: বছর (২০২৫/২০২৬), ৫ দিনের কর্মসূচি, অংশগ্রহণকারী (৬০৩ জন), ৬টি গ্রুপ এবং ৬টি স্টেশন।"))
    body_elements.append(create_bullet("লাইভ কাউন্টার কার্ড: মোট অংশগ্রহণকারী (৬০৩), আজকের মোট হাজিরা শতকরা হার, বর্তমান রানিং সেশন এবং সক্রিয় জরুরি কেস।"))
    body_elements.append(create_bullet("গ্রুপ পয়েন্ট লিডারবোর্ড: কুরআন তেলাওয়াত, খেলাধুলা, বিতর্ক, দেয়াল পত্রিকা ও নিয়মানুবর্তিতা থেকে প্রাপ্ত নম্বরের লাইভ র‌্যাঙ্কিং।"))
    body_elements.append(create_bullet("কুইক একশন বাটন: ইউনিভার্সাল কিউআর স্ক্যানার, বাল্ক এক্সেল ইমপোর্টার, এডমিন আনলক এবং ভাষা পরিবর্তন (বাংলা/English)।"))

    # ৩. অংশগ্রহণকারী ও দল ব্যবস্থাপনা
    body_elements.append(create_p("৩. অংশগ্রহণকারী ও দল–সাবগ্রুপ ব্যবস্থাপনা (Participants & Subgroup Management)", bold=True, size=28, color="0F5132", space_after=140))
    body_elements.append(create_p("ক্যাম্পের অংশগ্রহণকারীদের ৬টি মূল দলে এবং প্রতি দলে একাধিক কার্যকরী সাব-গ্রুপে ভাগ করা হয়েছে। প্রতিটি সাব-গ্রুপে একজন করে সিনিয়র মেন্টর দায়িত্ব পালন করেন।", size=22, space_after=140))
    
    headers_groups = ["গ্রুপের নাম (Group)", "দলনেতা (Leader)", "মোবাইল নম্বর", "সাবগ্রুপ ও মেন্টর তালিকা (Subgroups & Mentors)"]
    rows_groups = [
        ["১. বুখারী (Bukhari)", "উস্তাদ আহমাদুল্লাহ", "01711-000001", "Bukhari-1 (মারুফ), Bukhari-2 (শামীম), Bukhari-3 (রাকিব)"],
        ["২. মুসলিম (Muslim)", "মাওলানা তারিক জামিল", "01711-000002", "Muslim-1 (হাসান), Muslim-2 (আসিফ), Muslim-3 (মিজান)"],
        ["৩. আবু দাউদ (Abu Dawood)", "হাফেজ তানভীর", "01711-000003", "AbuDawood-1 (জুবায়ের), AbuDawood-2 (তাহমিদ)"],
        ["৪. তিরমিযী (Tirmidhi)", "মুফতি সালমান", "01711-000004", "Tirmidhi-1 (কাওসার), Tirmidhi-2 (রিয়াদ)"],
        ["৫. সালাহউদ্দীন আইয়ুবী", "ইঞ্জি. খালিদ মাহমুদ", "01711-000005", "Salahuddin-1 (শাকিল), Salahuddin-2 (মুরাদ), Salahuddin-3 (বশির)"],
        ["৬. সুমাইয়া (Sumaiya)", "ডা. নাসিমা সুলতানা", "01711-000006", "Sumaiya-1 (ফাতেমা), Sumaiya-2 (মরিয়ম), Sumaiya-3 (আয়েশা)"]
    ]
    body_elements.append(create_table(headers_groups, rows_groups))

    body_elements.append(create_bullet("ডিজিটাল আইডি কার্ড: প্রত্যেক অংশগ্রহণকারীর ছবি, নাম, রোল, ক্যাম্প আইডি (যেমন LSC-2026-015), রক্তের গ্রুপ, অভিভাবকের ফোন এবং কিউআর কোড রয়েছে।"))
    body_elements.append(create_bullet("গ্রুপ ও সাবগ্রুপ ফিল্টারিং: তালিকা স্ক্রিনে এক ক্লিকেই নির্দিষ্ট সাবগ্রুপের শিক্ষার্থীদের আলাদা করে তালিকা দেখা ও হাজিরা যাচাই করা যায়।"))
    body_elements.append(create_bullet("এডমিন মোডে মেন্টর ও লিডার এডিট: অ্যাডমিন পাসওয়ার্ড (admin123) দিয়ে আনলক করে যেকোনো দলের নেতা ও মেন্টরের নাম ও ফোন আপডেট করা যায়।"))

    # ৪. পাঁচ দিনের কর্মসূচি ও স্টেশন
    body_elements.append(create_p("৪. পাঁচ দিনের কর্মসূচি, স্টেশন ও রোটেশন (5-Day Schedule & Station Rotations)", bold=True, size=28, color="0F5132", space_after=140))
    body_elements.append(create_p("ক্যাম্পের পাঁচ দিনের কর্মসূচি সকাল ৫:০০ টা থেকে রাত ১০:০০ টা পর্যন্ত সুনির্দিষ্টভাবে বিন্যস্ত। এর মধ্যে রয়েছে ৬টি বিশেষায়িত স্টেশন রোটেশন।", size=22, space_after=140))
    
    headers_stations = ["স্টেশন আইডি", "স্টেশনের নাম", "স্থান (Venue)", "ফোকাস ও উপকরণ"]
    rows_stations = [
        ["Station 1", "কুরআন ও তাজবীদ", "মসজিদ কমপ্লেক্স", "বিশুদ্ধ উচ্চারণ, মাখরাজ ও অর্থানুবাদ"],
        ["Station 2", "আত্মরক্ষা ও ফিটনেস", "মূল খেলার মাঠ", "বেসিক ডিফেন্স, ওয়ার্মআপ, ক্যারাটে মুভস"],
        ["Station 3", "ফার্স্ট এইড ও উদ্ধার", "হল রুম ১", "সিপিআর, ব্যান্ডেজ, ফায়ার ড্রিল ও স্ট্রেচার"],
        ["Station 4", "টিমওয়ার্ক ও কমিউনিকেশন", "মুক্তমঞ্চ প্রাঙ্গণ", "রোল প্লে, মাইন্ড ম্যাপ, দলগত যোগাযোগ"],
        ["Station 5", "প্রব্লেম সলভিং ও বিজ্ঞান", "অডিটোরিয়াম", "ব্রেইনস্টর্মিং, কেস স্টাডি ও কুইজ"],
        ["Station 6", "নৈতিক নেতৃত্ব ও চরিত্র", "সম্মেলন কক্ষ ২", "উসওয়ায়ে হাসানাহ, শিষ্টাচার ও নেতৃত্ব"]
    ]
    body_elements.append(create_table(headers_stations, rows_stations))

    body_elements.append(create_bullet("দৈনিক কার্যক্রমের ব্যতিক্রম (Schedule Exception / Changes): আবহাওয়া বা অনিবার্য কারণে সেশন পরিবর্তন হলে এডমিন সরাসরি সেশনের সময়, শিরোনাম ও ভেন্যু এডিট করতে পারেন।"))
    body_elements.append(create_bullet("রোটেশন ম্যাট্রিক্স: দিন ১ থেকে দিন ৫ এর ট্যাবভিত্তিক ভিউতে স্বয়ংক্রিয়ভাবে দেখায় কোন সেশনে কোন গ্রুপ কোন স্টেশনে থাকবে।"))

    # ৫. কর্মকর্তা, স্বেচ্ছাসেবক ও দায়িত্ব বণ্টন
    body_elements.append(create_p("৫. কর্মকর্তা, স্বেচ্ছাসেবক ও দায়িত্ব বণ্টন (Staff, Duty Roster & Volunteers)", bold=True, size=28, color="0F5132", space_after=140))
    body_elements.append(create_p("ক্যাম্পের নির্বিঘ্ন পরিচালনার জন্য বিভিন্ন কমিটি এবং দায়িত্ব রোস্টার প্রণয়ন করা হয়েছে। অ্যাপে রয়েছে দায়িত্বপ্রাপ্ত কর্মকর্তা ও ভলান্টিয়ার ট্র্যাকিং।", size=22, space_after=140))
    body_elements.append(create_bullet("কমিটি সমূহ: ক্যাম্প শৃঙ্খলা কমিটি, খাদ্য ও আপ্যায়ন কমিটি, স্বাস্থ্য ও প্রাথমিক চিকিৎসা কমিটি, প্রতিযোগিতা কমিটি, আইটি ও মিডিয়া কমিটি।"))
    body_elements.append(create_bullet("ডিউটি রোস্টার: নির্দিষ্ট সময়ে কোন কর্মকর্তা কোন পয়েন্টে (গেট, ডাইনিং, হল, মাঠ) দায়িত্বে থাকবেন তা সংরক্ষিত থাকে।"))
    body_elements.append(create_bullet("এক্সেল বাল্ক ইমপোর্ট: এক্সেল বা গুগল শিট থেকে কপি করে বা CSV ফাইল আপলোড করে এক ক্লিকে সকল ভলান্টিয়ার ও ডিউটি অফিসারের ডাটা এন্ট্রি করা যায়।"))

    # ৬. হাজিরা, খাবার ও কার্যক্রমে অংশগ্রহণ
    body_elements.append(create_p("৬. হাজিরা, খাবার ও কার্যক্রমে অংশগ্রহণ (QR Attendance & Meal Control)", bold=True, size=28, color="0F5132", space_after=140))
    body_elements.append(create_p("ক্যাম্পের অন্যতম প্রধান চ্যালেঞ্জ হলো ৬০৩ জনের নির্ভুল উপস্থিতি ও খাবার বণ্টন তদারকি। অ্যাপে এটি শতভাগ অটোমেটেড।", size=22, space_after=140))
    body_elements.append(create_bullet("হাজিরা চেক-ইন: কিউআর কোড স্ক্যান করার সাথে সাথে নির্দিষ্ট জামায়াত বা সেশনে উপস্থিতি রেকর্ড হয়ে যায়।"))
    body_elements.append(create_bullet("খাবার গ্রহণ ও ডাবল ক্লেইম রোধ: সকালের নাস্তা, দুপুরের খাবার বা রাতের খাবারের সময় স্ক্যান করলে সিস্টেম জানিয়ে দেয় ইতিমধ্যে খাবার নিয়েছে কি না। ফলে অপচয় ও অনিয়ম সম্পূর্ণ বন্ধ হয়।"))
    body_elements.append(create_bullet("ম্যানুয়াল ওভাররাইড: কিউআর কোড নষ্ট হলে বা শিক্ষার্থী ব্যাজ ভুলে ফেলে আসলে কোড দিয়ে সার্চ করে কারণ উল্লেখপূর্বক ম্যানুয়াল এন্ট্রি সম্ভব।"))

    # ৭. আবাসন, কক্ষ ও ভ্রমণ ব্যবস্থাপনা
    body_elements.append(create_p("৭. আবাসন, কক্ষ ও ভ্রমণ ব্যবস্থাপনা (Accommodation, Rooms & Travel)", bold=True, size=28, color="0F5132", space_after=140))
    body_elements.append(create_p("পিডিএফ ডকুমেন্টের পরিকল্পনা অনুযায়ী ৬০৩ জন শিক্ষার্থীর জন্য সুপরিকল্পিত কক্ষ বরাদ্দ ও পরিবহন ব্যবস্থা সংযুক্ত রয়েছে।", size=22, space_after=140))
    body_elements.append(create_bullet("কক্ষ ও বেড ম্যাপিং: ভবন (Block A, Block B, Block C), রুম নম্বর (১০১-১১০) এবং বেড নম্বর অনুযায়ী শিক্ষার্থী ট্যাগ করা থাকে।"))
    body_elements.append(create_bullet("ভ্রমণ ও আগমন-প্রস্থান ট্র্যাকিং: বাস/যানবাহন নম্বর, ড্রপ পয়েন্ট, আগমনের সময় ও ক্যাম্প সমাপ্তিতে নিরাপদে অভিভাবকদের কাছে হস্তান্তর স্ট্যাটাস চেক।"))

    # ৮. প্রতিযোগিতা, বিচার, পুরস্কার ও সনদ
    body_elements.append(create_p("৮. প্রতিযোগিতা, বিচারক প্যানেল, পুরস্কার ও সনদ (Competitions, Scoring & Awards)", bold=True, size=28, color="0F5132", space_after=140))
    body_elements.append(create_bullet("প্রতিযোগিতার ধরণ: ক্বিরাত, হাদিস মুখস্থ, আজান, ইসলামি কুইজ, ক্যালিগ্রাফি, বিতর্ক ও স্পোর্টস।"))
    body_elements.append(create_bullet("১০০ নম্বরের স্মার্ট স্কোরিং: বিশুদ্ধতা ও নির্ভুলতা (৪০), উপস্থাপনা ও সুর (৩০), এবং সময় ও শৃঙ্খলা (৩০) এর উপর ভিত্তি করে বিচারকগণ সরাসরি স্লাইডারে নম্বর দেন।"))
    body_elements.append(create_bullet("বিচারকমণ্ডলী ম্যানেজমেন্ট: বিচারক যুক্ত করা, এডিট করা, ক্যাটাগরি নির্ধারণ এবং এক্সেল থেকে বাল্ক ইমপোর্ট সুবিধা।"))
    body_elements.append(create_bullet("উপহার ও কিট বিতরণ ইনভেন্টরি: ট্রফি, মেডেল, বই, ক্রেস্ট এবং ক্যাম্প কিট (ব্যাগ, খাতা, কলম, টি-শার্ট) স্টক হিসাব ও ডিজিটাল বিতরণ ভাউচার।"))
    body_elements.append(create_bullet("অটোমেটিক সমাপ্তি সনদ (Certificate): সফল শিক্ষার্থীদের জন্য ক্যাম্প চিফের স্বাক্ষরযুক্ত ডিজিটাল সনদ প্রস্তুত ও প্রিন্ট উপযোগী ভিউ।"))

    # ৯. দেয়াল পত্রিকা, বুলেটিন ও জরুরি সেবা
    body_elements.append(create_p("৯. দেয়াল পত্রিকা, বুলেটিন ও জরুরি সেবা (Wall Magazine, Bulletin & Emergency)", bold=True, size=28, color="0F5132", space_after=140))
    body_elements.append(create_bullet("সরাসরি বুলেটিন প্রকাশ: এডমিন মোড থেকে '+ সরাসরি নতুন বুলেটিন প্রকাশ করুন' বাটনে চাপ দিয়ে শিরোনাম, ক্যাটাগরি ও বিবরণ দিয়ে সাথে সাথে নোটিশবোর্ডে লাইভ করা যায়।"))
    body_elements.append(create_bullet("দেয়াল পত্রিকা হাইলাইট: দিনভিত্তিক দেয়াল পত্রিকার সেরা লেখা ও সেরা সাব-গ্রুপকে স্পটলাইট ব্যাজে প্রদর্শন।"))
    body_elements.append(create_bullet("জরুরি মেডিকেল ও ইনসিডেন্ট: তাৎক্ষণিক ফার্স্ট এইড লগিং, প্রেসার ও পালস রেকর্ড এবং ক্যাম্প সিকিউরিটি হেল্পলাইনে সরাসরি কল সুবিধা।"))

    # ১০. প্রযুক্তিগত আর্কিটেকচার, নিরাপত্তা ও এক্সেল ইমপোর্ট
    body_elements.append(create_p("১০. প্রযুক্তিগত আর্কিটেকচার, নিরাপত্তা ও এক্সেল ইমপোর্ট (Architecture, Security & Batch Import)", bold=True, size=28, color="0F5132", space_after=140))
    body_elements.append(create_bullet("কোটলিন ও জেটপ্যাক কম্পোজ: শতভাগ আধুনিক ডিক্লারেটিভ ইউআই এবং রেসপনসিভ মেটেরিয়াল ৩ ডিজাইন।"))
    body_elements.append(create_bullet("রুম ডাটাবেজ (Room SQLite): ডিভাইস স্তরে ডেটা স্থায়ীভাবে সংরক্ষিত থাকে; অ্যাপ বন্ধ বা ফোন রিস্টার্ট হলেও কোনো ডেটা বিনষ্ট হয় না।"))
    body_elements.append(create_bullet("এডমিন পাসওয়ার্ড প্রটেকশন: টাইম, সেশন, গ্রুপ লিডার, মেন্টর, বিচারক এডিট বা ডিলিটের জন্য 'admin123' বা 'camp2026' সিকিউরিটি লক।"))
    body_elements.append(create_bullet("ইউনিভার্সাল এক্সেল বাল্ক ইমপোর্টার: CSV ফাইল নির্বাচন অথবা এক্সেল থেকে কপি-পেস্ট করে কয়েক সেকেন্ডে শত শত রেকর্ড ডেটাবেজে যুক্ত করা যায়।"))

    # Conclusion
    body_elements.append(create_p("উপসংহার (Conclusion)", bold=True, size=28, color="0F5132", space_after=140))
    body_elements.append(create_p("এই পূর্ণাঙ্গ নির্দেশিকা এবং অ্যান্ড্রয়েড অ্যাপ্লিকেশনটি LSC 2025/2026 ক্যাম্পের প্রতিটি প্রশাসনিক স্তরকে শৃঙ্খলিত, ডিজিটাল এবং শতভাগ নির্ভুল করবে। এটি একটি যুগান্তকারী ক্যাম্প সহচর অ্যাপ হিসেবে মাঠপর্যায়ে সফলভাবে বাস্তবায়িত।", size=22, space_after=200))

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
    build_docx('/app/applet/LSC_Camp_Companion_Documentation.docx')
    build_docx('/app/applet/LSC_2025_Camp_Companion_Full_Documentation.docx')
