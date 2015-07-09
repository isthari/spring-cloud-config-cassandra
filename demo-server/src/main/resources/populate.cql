use cloud_config;

insert into application_label_version (application, label , profile, version) values ('app1', 'master', 'devel', 45f59300-264c-11e5-a695-9d4a736a4a7c);
insert into configuration_snapshot(application, version, parameters) values ('app1', 45f59300-264c-11e5-a695-9d4a736a4a7c, {'param1':'value1', 'param2':'value2'});

insert into application_label_version (application, label , profile, version) values ('app1', 'master', '', 45f59300-264c-11e5-a695-9d4a736a4a7d);
insert into configuration_snapshot(application, version, parameters) values ('app1', 45f59300-264c-11e5-a695-9d4a736a4a7d, {'param3':'value3'});

insert into application_label_version (application, label , profile, version) values ('application', 'master', '', 45f59300-264c-11e5-a695-9d4a736a4a7e);
insert into configuration_snapshot(application, version, parameters) values ('application', 45f59300-264c-11e5-a695-9d4a736a4a7e, {'param4':'value4'});

