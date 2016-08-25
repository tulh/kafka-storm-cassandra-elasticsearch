from cassandra.auth import PlainTextAuthProvider
from cassandra.cluster import Cluster
from dateutil import parser
import json
import requests

authentication = PlainTextAuthProvider(username='cassandra', password='cassandra')
cluster = Cluster(['172.16.10.254', '172.16.10.123', '172.16.10.124'], port=9042, auth_provider=authentication)
session = cluster.connect('hocvalam')

date = raw_input('Enter date to import - format YYYY-mm-dd HH:MM:SS(leave empty for full import): ')
if date is not '':
    date = parser.parse(date)
    print('Do import post from: ' + str(date))
    rows = session.execute('select * from post where date_created > %s allow filtering;', parameters=[date])
else:
    print('Do full import')
    rows = session.execute('select * from post;')
for row in rows:
    # print row.post_id, row.author_id, row.date_created, row.content
    url = 'http://172.16.10.132:9200/hocvalam-social/post/' + str(row.post_id)
    payload = {'post_id': str(row.post_id), 'author_id': str(row.author_id),
               'created_date': str(row.date_created), 'content': row.content}
    r = requests.post(url, data=json.dumps(payload))
    print r.text
