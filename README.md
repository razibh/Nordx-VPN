# Nordx-VPN
#Razib Hossain
https://github.com/razibh

# Nordx-VPN
How to open project

    open Android Studio > Open an Existing Android Studio Project > Select Your Project build.gradle file > ok

How to change app name

    open Android Studio >app > res > values > strings.xml
    enter your app name inside "app_name" string tag

https://github.com/razibh/Nordx-VPN

How to change app icon

    open Android Studio > app > res > all mipmap folder > ic_launcher.png
    rename your icon with ic_launcher.png and paste in mipmap folder
    replace all icons with particular size in particular folder

How to Change App Logo

    select project > go to Res > drawable
    Inside the drawable You will See logo.png You need to Replace that with your logo.png

SDK Required OneConnect API Key, You need to Buy SDK Server Plan according to your needs
 Visit on this Website https://developer.oneconnect.top and Create Account and get the Key.

 Once You have the OneConnect API Key, Then You have to Put inside String.xml 
 You will Get the Key inside API Tab https://developer.oneconnect.top/dashboard/api/ 
 After that, You need to Put Your App Package Name Inside OneConnect API Tab.



 Firebase Configuration
1. Setup and create firebase project

You must create a New Project in Firebase by logging into Firebase Console and then entering using your Google account if you don't sign in, then create a new Project and specify the name you like for the project, then:

    click on Add Firebase to your Android app
    copy the application Id from your build.gradle and paste it as Android Package Name

    click on Register App > Next
    click on Download google-services.json. it will download a .json file we will use it later
you can skip the remaining setup steps

next up copy the downloaded google-services.json and paste it in the path 'fast_vpn_app\app\' to make sure that your app is linked with your Firebase Project

 Setup firebase realtime database
Change database rules

    click on Database > Realtime Database > Rules
    change ".read": false and ".write": false to ".read": true and ".write": true

https://github.com/razibh/Nordx-VPN

Setup In App Subscription
How to Setup In App Subscription

    Go to Your Google Play Conse.
    Create Subscription I'd inside Subscription.
    After Create I'd Go to Your android Project.`
    Inside Config.java Put Your All Product I'd.
    After that Need to Update the app in play Console.
