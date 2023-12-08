import pyrebase

config = {
  "apiKey": "AIzaSyC97FCgS9QpQqTUhWPiEOrV2xZZypIPnCY",
  "authDomain": "nstu-news.firebaseapp.com",
   "projectId": "nstu-news",
  "databaseURL": "https://nstu-news-default-rtdb.firebaseio.com",
  "storageBucket": "nstu-news.appspot.com",
  "messagingSenderId": "849506019932",
    "appId": "1:849506019932:web:b69a20a0bbc26f0b74d499",
   "measurementId": "G-Y2XSCCWJKZ"
}

firebase = pyrebase.initialize_app(config)