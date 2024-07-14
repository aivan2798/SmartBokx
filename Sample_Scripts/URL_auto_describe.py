from bs4 import BeautifulSoup
import requests,re
headers = {
    'User-Agent': 'smartbokxBot',
}
url_request = requests.get('https://www.monitor.co.ug/uganda/news/world/trump-shot-in-right-ear-at-campaign-rally-shooter-dead-4689694',headers=headers)
#url_request
#stackoverflow https://stackoverflow.com/questions/33330483/request-only-meta-tags-from-a-webpage
##teng,teng https://www.tiktok.com/@iamrangotengetenge/video/7388430992769355054?is_from_webapp=1&sender_device=pc
soup = BeautifulSoup(url_request.content, 'html.parser')
url_title = soup.title.text
url_description = soup.find('meta',attrs={'name':re.compile('.*description')}).get('content')

print("url title: ", url_title)
print("url description: ", url_description)