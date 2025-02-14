package com.contactservice.service;

import com.contactservice.dto.GroupDTO;
import com.contactservice.models.Contact;
import com.contactservice.models.ContactGroup;
import com.contactservice.models.Group;
import com.contactservice.repository.ContactGroupRepository;
import com.contactservice.repository.ContactRepository;
import com.contactservice.repository.GroupsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContactService {
    @Autowired
    private ContactGroupRepository contactGroupRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
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
               return ContactGroup.builder().groupId(groupDAO.getGroupId()).contactId(contact.getContactId()).
                       contactGroupId(UUID.randomUUID().toString()).build();
           })).collect(Collectors.toUnmodifiableList());
        contactGroupRepository.saveAll(contactGroups);
       }
       return groupDAO.getGroupId();
    }

    public List<GroupDTO> readGroups(String userId){
        List<Group> groups = groupsRepository.findAllByUserId(userId);
        List<GroupDTO> groupDTOS = new ArrayList<>();
        groups.forEach((group) ->{
            List<ContactGroup> contactGroups = contactGroupRepository.findAllByGroupId(group.getGroupId());
            List<Contact> contacts = new ArrayList<>();
            contactGroups.forEach((contactGroup)->{
                Optional<Contact> contact = contactRepository.findById(contactGroup.getContactId());
                contact.ifPresent(contacts::add);
            });
            groupDTOS.add(GroupDTO.builder()
                    .userId(group.getUserId())
                    .groupName(group.getGroupName())
                    .contacts(contacts)
                    .build());
        });
        return groupDTOS;
    }
    public Optional<GroupDTO> readGroup(String groupId){
        Optional<Group> contactGroup = groupsRepository.findById(groupId);
        System.out.println(contactGroup.get());
        if(contactGroup.isPresent()){
            List<ContactGroup> contactGroups = contactGroupRepository.findAllByGroupId(contactGroup.get().getGroupId());
            List<Contact> contacts = new ArrayList<>();
            contactGroups.forEach((group)->{
                Optional<Contact> contact = contactRepository.findById(group.getContactId());
                contact.ifPresent(contacts::add);
            });
            /*Now map the contact groups to a GroupDTO*/
            return Optional.of(GroupDTO.builder()
                    .userId(contactGroup.get().getUserId())
                            .groupName(contactGroup.get().getGroupName())
                            .contacts(contacts)
                    .build());
        }else{
            throw new RuntimeException("Contact Group Doesn't exist : 404");
        }
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

