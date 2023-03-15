

# Test Plan

**Author**: Team 064 <br>
**Version**: 3.0 (Final Results)


## 1 Testing Strategy


### 1.1 Overall strategy

We will perform unit, and manual regression testing on our application. 



*   Unit tests will be added for logic heavy functions (e.g. calculating job ranks and adjusted compensation numbers) in non UI activity classes.
*   In addition to unit tests that will be run before merging each pull request, we will also perform manual regression tests. We will use our list of use cases to help us walk through the app and make sure our change didn’t break anything.
    *   These tests will be performed by the code author before merging their pull request.


### 1.2 Test Selection

If a user can perform a certain action, it should be tested. UI testing would be a black-box test, and typically lends itself more to an system test, but could also be unit tested in a more limited capacity. We’ll take a look at our use cases, edge cases and error states, causes and effects, and state transitions to define test cases.

Any significant logic (e.g. calculations) should be tested as well, also using black-box testing. We’ll choose specific test cases by checking boundary conditions and error states, combinatorial testing (i.e. testing all classes of inputs to verify outputs). These would be directly tested with unit tests and indirectly tested in an system tests. We will also utilize white-box testing in our unit tests by examining the flow of data and ensuring coverage of each branch.


### 1.3 Adequacy Criterion

We will use a few different techniques to make sure our test cases are adequate:



*   We will run manual regression tests, and if we catch any bugs with the manual tests that weren’t caught with a unit or integration test, we will add a unit or integration test (as appropriate) to capture that use case.
*   We can use slack to communicate and address bugs found during development. The appropriate developer will be identified to address issues that arise and communication with that developer will occur in slack.
*   Lastly, we can utilize [Android Studio to track test coverage](https://developer.android.com/studio/test#view_test_coverage). When running tests in the Android Studio UI, we can select `Run $test with coverage`. This provides a coverage tool window that shows what percentage of classes, methods, and lines have test coverage. We should aim for 80% line coverage of non activity classes, given the time constraints of this project.


### 1.4 Bug Tracking

We will utilize the Slack to record bugs found during development and testing. These bugs will be pinned to the channel in a message and communication will occur with the appropriate developer to resolve the bug.


### 1.5 Technology

We will use JUnit to test the logic of our app. 


## 2 Test Cases

These test cases will be used for regression testing, as well as the basis for the automated integration tests.

|#|Purpose|Steps|Expected result|Actual result|Pass/Fail|Notes|
|--- |--- |--- |--- |--- |--- |--- |
|1|Initial state|Start the app|User sees main menu to enter or edit current job details, enter job offers, adjust the comparison settings, or compare job offers (disabled until a job offers exist)|User sees main menu to enter or edit current job details, enter job offers, adjust the comparison settings, or compare job offers (disabled before a job offers exist)|Pass||
|2|Menu state with current job and one job offer|Enter current job details and one job offer, then open main menu|Option to compare job offers is enabled|Option to compare job offers is enabled|Pass|(Delete app storage to clear jobs if needed)|
|3|Menu state with 2 job offers|Enter two job offers, then open main menu|Option to compare job offers is enabled|Option to compare job offers is enabled|Pass|(Delete app storage to clear jobs if needed)|
|4|Entering current job details|Tap "current job"|User is given a form to fill in all job details. Missing fields report errors when save is clicked. Save and cancel both return to the main menu. Cancel does not update the current job.|User is given a form to fill in all job details. Save and cancel both return to the main menu. Cancel does not update the current job.|Pass||
|5|Entering current job details 2|Tap "current job" after having saved a current job|User is given a form to fill in all job details. Details should be pre-filled. Save and cancel both return to the main menu. Cancel does not update the current job.|User is given a form to fill in all job details. Details should be pre-filled. Save and cancel both return to the main menu. Cancel does not update the current job.|Pass||
|6|Entering job offers|Tap "enter job offers". Fill out some details and click cancel.|User is given a form to fill in all job details. Cancel does not update the current job and returns to the main menu.|User is given a form to fill in all job details. Cancel does not update the current job and returns to the main menu.|Pass||
|7|Entering first job offer without saved current job|Tap "enter job offers". Fill out details and click save. Click button to enter another job offer.|User is given a form to fill in all job details.  Missing fields report errors when save is clicked. Button to enter another job brings the user back to an empty form. Menu button returns to main menu. Compare Jobs button is disabled on the main menu.|User is given a form to fill in all job details. Missing fields report errors when save is clicked. Button to enter another job brings the user back to an empty form. Menu button returns to main menu. Compare Jobs button is disabled on the main menu.|Pass||
|8|Entering job offers with a current job.|Enter a current job. Tap "enter job offers". Fill out details and click save.|User is given a form to fill in all job details. Missing fields report errors when save is clicked. After saving, compare with current job is enabled. Menu button returns to main menu.|User is given a form to fill in all job details. Missing fields report errors when save is clicked. After saving, compare with current job is enabled. Menu button returns to main menu.|Pass||
|9|Enter comparison settings|Tap "comparison settings" then tap "cancel"|User is given a form with all weight fields, all initially set to 1. Cancel does not save the updated weights.|User is given a form with all weight fields, all initially set to 1. Cancel does not save the updated weights.|Pass||
|10|Adjust comparison settings|Tap "comparison settings". Edit values and tap save. Tap "comparison settings" again.|User is given a form with all weight fields, all with the previously entered values.|User is given a form with all weight fields, all with the previously entered values.|Pass||
|11|See ranking without current job or job offers|Tap "compare job offers"|User is shown a list of job offers, ranked from best to worst. Select buttons appear on each row.|User is shown a list of job offers, ranked from best to worst. Select buttons appear on each row.|Pass||
|12|See ranking with current job and a job offer|Enter a current job and one job offer. Tap "compare job offers"|User is shown a list of job offers, ranked from best to worst, and including the current job, clearly indicated. Select buttons appear on each row.|User is shown a list of job offers, ranked from best to worst, and including the current job, clearly indicated. Select buttons appear on each row.|Pass||
|13|Compare two jobs|Tap "compare job offers". Select 2 offers|User is shown a table comparing the two jobs displaying job details. Buttons available to go back to main menu and to perform another comparison|User is shown a table comparing the two jobs displaying job details. Buttons available to go 'back' to perform another comparison and 'cancel' to return to the main menu.|Pass||
|14|Job rank calculations|Enter 3+ job offers. Tap "compare job offers". Go back to main menu. Tap "comparison settings", tweak weights, save. Tap "compare job offers".|Job listing order has updated after changing the weights.|Job listing order has updated after changing the weights.|Pass||
|15|Session Persistence|After completing Test 14, force close the app, then restart the app. Tap "current job", then "cancel", then "compare job offers", then "cancel", then "comparison settings".|All previously entered information is present.|All previously entered information is present.|Pass||
|16|All non activity classes unit tested |Run all unit test suites.| All unit tests pass with over 80% code coverage.|All unit tests pass with over 80% code coverage.|Pass|ComparisonSettings class = 100% code coverage, Job class = 100% code coverage|

