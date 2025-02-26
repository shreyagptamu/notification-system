class NotificationPreference{
    userId;
    notificationType;
    static notificationSchedule = class{
        groupId;
        recurrence;
        triggerTime;
        message;
    };
}

class Contact{
    emailId;
    phoneNo;
    name;
}

class ContactGroup{
    userId;
    groupName;
    static contacts = class{
        emailId;
        phoneNo;
        name;
    };
}

export {NotificationPreference, Contact, ContactGroup};