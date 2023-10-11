MERGE INTO hits AS h USING (VALUES (1, 'ewm-main-service', '/events/1', '192.163.0.1', '2004-10-19 10:23:54'),
(1, 'ewm-main-service', '/events/1', '192.163.0.1', '2004-10-19 10:23:54'),
(1, 'ewm-main-service', '/events/1', '192.163.0.1', '2004-10-19 10:23:54')) s(hits_id, hits_app, hits_uri, hits_ip, hits_timestamp)
ON h.id = s.hits_id AND h.app = s.hits_app AND h.uri = s.hits_uri AND h.ip = s.hits_ip AND h.timestamp = s.hits_timestamp
WHEN NOT MATCHED THEN INSERT VALUES (s.hits_id, s.hits_app, s.hits_uri, s.hits_ip, s.hits_timestamp);