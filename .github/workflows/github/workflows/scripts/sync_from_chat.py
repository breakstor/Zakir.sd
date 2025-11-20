#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import requests
import json
from github import Github
import base64

class ChatSyncManager:
    def __init__(self, github_token):
        self.github = Github(github_token)
        self.repo = self.github.get_repo("your-username/sudanese-student-assistant")
        
        # تعريف هيكل الملفات المطلوبة
        self.required_files = {
            # ملفات Java
            "android_project/app/src/main/java/com/example/studentassistant/MainActivity.java": "java",
            "android_project/app/src/main/java/com/example/studentassistant/AdvancedChatDataLoader.java": "java",
            "android_project/app/src/main/java/com/example/studentassistant/ChatModel.java": "java",
            "android_project/app/src/main/java/com/example/studentassistant/QuizModel.java": "java",
            "android_project/app/src/main/java/com/example/studentassistant/QuizActivity.java": "java",
            "android_project/app/src/main/java/com/example/studentassistant/FormulaAdapter.java": "java",
            "android_project/app/src/main/java/com/example/studentassistant/ChatSession.java": "java",
            "android_project/app/src/main/java/com/example/studentassistant/ChatMessage.java": "java",
            "android_project/app/src/main/java/com/example/studentassistant/ChatHistoryDBHelper.java": "java",
            "android_project/app/src/main/java/com/example/studentassistant/ChatHistoryActivity.java": "java",
            "android_project/app/src/main/java/com/example/studentassistant/ChatSessionAdapter.java": "java",
            
            # ملفات XML
            "android_project/app/src/main/res/layout/activity_main.xml": "xml",
            "android_project/app/src/main/res/layout/activity_quiz.xml": "xml", 
            "android_project/app/src/main/res/layout/activity_chat_history.xml": "xml",
            "android_project/app/src/main/res/layout/item_chat_session.xml": "xml",
            "android_project/app/src/main/res/values/strings.xml": "xml",
            "android_project/app/src/main/res/values/colors.xml": "xml",
            "android_project/app/src/main/res/values/styles.xml": "xml",
            
            # ملفات التكوين
            "android_project/build.gradle": "gradle",
            "android_project/app/build.gradle": "gradle", 
            "android_project/app/src/main/AndroidManifest.xml": "xml",
            
            # ملفات البيانات
            "android_project/app/src/main/assets/enhanced_chat_data.json": "json",
            
            # التوثيق
            "README.md": "markdown",
            "documentation/installation_guide.md": "markdown",
            "documentation/user_manual.md": "markdown",
            "documentation/api_reference.md": "markdown"
        }
    
    def extract_content_from_chat(self, file_type, file_name):
        """
        هذه الدالة ستحتاج لتعديلها حسب مصدر المحادثة
        حالياً نعود محتوى تجريبي - ستحتاج لربطها بمصدر حقيقي
        """
        # محتوى تجريبي للملفات (ستستبدله بالمحتوى الحقيقي)
        sample_content = {
            "java": f"// {file_name}\npublic class Sample {{ \n    // محتوى حقيقي سيأتي من المحادثة\n}}",
            "xml": f"<!-- {file_name} -->\n<layout>\n    <!-- محتوى حقيقي سيأتي من المحادثة -->\n</layout>",
            "json": f"// {file_name}\n{{\n  \"data\": \"محتوى حقيقي سيأتي من المحادثة\"\n}}",
            "markdown": f"# {file_name}\n\nمحتوى حقيقي سيأتي من المحادثة",
            "gradle": f"// {file_name}\nplugins {{\n    // محتوى حقيقي سيأتي من المحادثة\n}}"
        }
        
        return sample_content.get(file_type, f"# {file_name}\n\nمحتوى {file_type}")

    def create_or_update_file(self, file_path, content):
        """إنشاء أو تحديث ملف في المستودع"""
        try:
            # محاولة الحصول على الملف إذا كان موجوداً
            file = self.repo.get_contents(file_path)
            # إذا الملف موجود، قم بتحديثه
            self.repo.update_file(
                file_path,
                f"🔄 تحديث {os.path.basename(file_path)}",
                content,
                file.sha
            )
            print(f"✅ تم تحديث: {file_path}")
        except:
            # إذا الملف غير موجود، قم بإنشائه
            self.repo.create_file(
                file_path,
                f"📝 إنشاء {os.path.basename(file_path)}", 
                content
            )
            print(f"✅ تم إنشاء: {file_path}")

    def sync_all_files(self):
        """مزامنة جميع الملفات المطلوبة"""
        print("🚀 بدء مزامنة الملفات...")
        
        for file_path, file_type in self.required_files.items():
            try:
                # استخراج المحتوى من المحادثة
                content = self.extract_content_from_chat(file_type, os.path.basename(file_path))
                
                # إنشاء أو تحديث الملف
                self.create_or_update_file(file_path, content)
                
            except Exception as e:
                print(f"❌ خطأ في {file_path}: {str(e)}")
        
        print("🎉 اكتملت المزامنة!")

def main():
    # الحصول على التوكن من environment variables
    github_token = os.getenv('GITHUB_TOKEN')
    
    if not github_token:
        print("❌ لم يتم العثور على GITHUB_TOKEN")
        return
    
    # بدء المزامنة
    sync_manager = ChatSyncManager(github_token)
    sync_manager.sync_all_files()

if __name__ == "__main__":
    main()
