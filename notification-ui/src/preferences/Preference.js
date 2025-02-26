import { NotificationPreference, Contact, ContactGroup } from "./types";
import {
    Flex, Text, Table,
    Thead,
    Tbody,
    Tr,
    Th,
    Td,
    TableCaption,
    TableContainer,
    Button,
    Modal,
    ModalOverlay,
    ModalContent,
    ModalHeader,
    ModalFooter,
    ModalBody,
    ModalCloseButton,
    useDisclosure,
    Input,
    Select
} from "@chakra-ui/react";
import { MultiSelect } from 'chakra-multiselect'


function Preference() {
    const { isOpen, onOpen, onClose } = useDisclosure();
    const options = [
        { label: 'Option 1', value: 'option1' },
        { label: 'Option 2', value: 'option2' },
        { label: 'Option 3', value: 'option3' },
    ]

    return (
        <Flex direction={'column'} gap={'16px'} width={'100%'} height={'800px'}>
            <Text fontSize={'3xl'}>User Preferences</Text>
            <Flex justifyContent={'flex-end'}>
                <Button onClick={onOpen}>Create Preference</Button>
            </Flex>
            <TableContainer>
                <Table variant='simple'>
                    <TableCaption>User Preferences</TableCaption>
                    <Thead>
                        <Tr>
                            <Th>User ID</Th>
                            <Th>Notification Type</Th>
                            <Th>Group ID</Th>
                            <Th>Recurrence</Th>
                            <Th>Trigger Time</Th>
                            <Th>Message</Th>
                        </Tr>
                    </Thead>
                    <Tbody>
                        <Tr>
                            <Td>123</Td>
                            <Td>SMS</Td>
                            <Td>245</Td>
                            <Td>Weekly</Td>
                            <Td>9 AM</Td>
                            <Td>Good Morning</Td>
                        </Tr>
                        <Tr>
                            <Td>123</Td>
                            <Td>SMS</Td>
                            <Td>245</Td>
                            <Td>Weekly</Td>
                            <Td>9 AM</Td>
                            <Td>Good Morning</Td>
                        </Tr>
                        <Tr>
                            <Td>123</Td>
                            <Td>SMS</Td>
                            <Td>245</Td>
                            <Td>Weekly</Td>
                            <Td>9 AM</Td>
                            <Td>Good Morning</Td>
                        </Tr>
                    </Tbody>
                </Table>
            </TableContainer>
            <Modal isOpen={isOpen} onClose={onClose} width={'700px'}>
                <ModalOverlay />
                <ModalContent >
                    <ModalHeader>Create Preference</ModalHeader>
                    <ModalCloseButton />
                    <ModalBody width={'400px'}>
                        <Flex direction={'column'} gap={'16px'}>
                            <Flex gap={'16px'}>
                                <Text>Notification Type</Text>
                                <Input placeholder='SMS' />
                            </Flex>
                            <Flex gap={'16px'}>
                                <Text>Select contacts</Text>
                                <MultiSelect options={options}
                                         />
                            </Flex>
                            <Flex gap={'16px'}>
                                <Text>Recurrence</Text>
                                <Input placeholder='Recurrence' />
                            </Flex>
                            <Flex gap={'16px'}>
                                <Text>Trigger Time</Text>
                                <Input placeholder='Trigger Time' />
                            </Flex>
                            <Flex gap={'16px'}>
                                <Text>Message</Text>
                                <Input placeholder='Message' />
                            </Flex>
                        </Flex>
                    </ModalBody>

                    <ModalFooter>
                        <Button colorScheme='blue' mr={3} onClick={onClose}>
                            Save
                        </Button>
                    </ModalFooter>
                </ModalContent>
            </Modal>
        </Flex>
    )
}

export default Preference;