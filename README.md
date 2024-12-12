Further refactoring:
- Verify if properties are validated on the backend or if further validation is needed on client
  (this is generally preferred if they are mostly validated on server with multiple platforms (Android, iOS)
  so validation logic doesn't get fractured between platforms)
- Dependency injection to allow removal of overhead and logic around object creation
- id prefixes for backend logs, to ensure ids don't clash with local logs.
- async fetching of both types of logs together
- Unit tests for Repository and ViewModel methods
- UI testing for CallLog adapter and main UI
- Probably IRL, we use websockets, notifications, or other liveness to update logs instead of polling,
  so repository would be refactored to emit flows to update data to ViewModel from local query and on new api data
- Fragments or Compose :) so that we can request permissions and only get to call logs and fetching if we know we have permissions
- Sorting and filtering of call logs
- Actions in call logs
