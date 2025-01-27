package com.contactservice.service;

import com.contactservice.dto.GroupDTO;
import com.contactservice.models.Contact;
import com.contactservice.models.ContactGroup;
import com.contactservice.models.Group;
import com.contactservice.repository.ContactGroupRepository;
import com.contactservice.repository.ContactRepository;
import com.contactservice.repository.GroupsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContactService {

    private ContactGroupRepository contactGroupRepository;
    private ContactRepository contactRepository;
    private GroupsRepository groupsRepository;

    public String addContact(Contact contact) {
        Contact existingContact= contactRepository.findByEmailIdAndPhoneNo(contact.getEmailId(),contact.getPhoneNo());
        UUID uuid=UUID.randomUUID();
        contact.setContactId(uuid.toString());
        if(existingContact==null){
            contactRepository.save(contact);
        }else{
            throw new RuntimeException("Contact already exists");
        }
        return uuid.toString();
    }


    public List<Contact> getContacts(String userId) {
        List<Contact> contacts= contactRepository.findByUserId(userId);
        return contacts;
    }

    public String createGroup(GroupDTO group) {
        Group groupDAO= Group.builder().groupName(group.getGroupName()).groupId(UUID.
                randomUUID().toString()).userId(group.getUserId()).build();
        groupsRepository.save(groupDAO);
       if(group.getContacts()!=null && !group.getContacts().isEmpty()){
           List<ContactGroup> contactGroups=group.getContacts().stream().map((contact -> {
               return ContactGroup.builder().contactGroupId(groupDAO.getGroupId()).contactId(contact.getContactId()).
                       contactGroupId(UUID.randomUUID().toString()).build();
           })).collect(Collectors.toUnmodifiableList());
        contactGroupRepository.saveAll(contactGroups);
       }
       return groupDAO.getGroupId();
    }

    public void updateGroup(GroupDTO group, String groupId) {
        Boolean existingGroup=groupsRepository.existsById(groupId);
        if(!existingGroup){
            throw new RuntimeException("Group doesnt exist");
        }else{
        Group groupDAO= Group.builder().groupName(group.getGroupName()).groupId(groupId).userId(group.getUserId()).build();
        groupsRepository.save(groupDAO);
        if(group.getContacts()!=null && !group.getContacts().isEmpty()){
            List<ContactGroup> contactGroups=group.getContacts().stream().map((contact -> {
                return ContactGroup.builder().contactGroupId(groupDAO.getGroupId()).contactId(contact.getContactId()).
                        contactGroupId(UUID.randomUUID().toString()).build();
            })).collect(Collectors.toUnmodifiableList());
            contactGroupRepository.saveAll(contactGroups);
        }

    }
}
}

