<h1>Design Description - Jacob Hogenkamp</h1>

<strong>
1. When the app is started, the user is presented with the main menu, which allows the user to (1) enter or edit current job details, (2) enter job offers, (3) adjust the comparison settings, or (4) compare job offers (disabled if no job offers were entered yet).
</strong>
<p>
User is first shown a menu with the operations contained in the MainMenu class.
</p>

<strong>
2. When choosing to enter current job details, a user will:
   a. Be shown a user interface to enter (if it is the first time) or edit all of the details of their current job, which consist of:<br>
   i. Title<br>
   ii. Company<br>
   iii. Location (entered as city and state)<br>
   iv. Cost of living in the location (expressed as an index)<br>
   v. Yearly salary<br>
   vi. Yearly bonus<br>
   vii. Allowed weekly telework days (expressed as the number of days per week allowed for remote work, inclusively between 0 and 5)<br>
   viii. Leave time (vacation days and holiday and/or sick leave, as a single overall number of days)<br>
   ix. Number of company shares offered at hiring (valued at $1 per share and expressed as a number >= 0)<br>
   b. Be able to either save the job details or cancel and exit without saving, returning in both cases to the main menu.
</strong>
<p>
User will be directed to a menu built with the EntryMenu class with currentOrOffer set to True. This indicates that only the save, cancel and exit without saving actions are available (not the comparison). saveDetails() will call Jobs' updateCurrent(). This will check if currentJob exits, then update or contruct it accordingly.
</p>

<strong>
3. When choosing to enter job offers, a user will:
   Be shown a user interface to enter all of the details of the offer, which are the same ones listed above for the current job.<br>
   a. Be able to either save the job offer details or cancel.<br>
   b. Be able to (1) enter another offer, (2) return to the main menu, or (3) compare the offer (if they saved it) with the current job details (if present).
</strong>
<p>
Again, the EntryMenu will be generated with currentOrOffer set to False. This enables all the menu options (saveDetails, returnToMain, compareJob). The cancel option will be implemented by the GUI and just clear input fields. saveDetails() will trigger Jobs to construct a new Job with the input parameters. The new Job will be insertion sorted into Jobs.
</p>

<strong>
4. When adjusting the comparison settings, the user can assign integer weights to:
   . Yearly salary
   a. Yearly bonus<br>
   b. Allowed weekly telework days<br>
   c. Leave time<br>
   d. Shares offered<br>
   If no weights are assigned, all factors are considered equal.
</strong>
<p>
The Settings class stores the mentioned parameters and has them all initially set to 1.
</p>

<strong>
5. When choosing to compare job offers, a user will:
   a. Be shown a list of job offers, displayed as Title and Company, ranked from best to worst (see below for details), and including the current job (if present), clearly indicated.<br>
   b. Select two jobs to compare and trigger the comparison.<br>
   c. Be shown a table comparing the two jobs, displaying, for each job:<br>
   i. Title<br>
   ii. Company<br>
   iii. Location<br>
   iv. Yearly salary adjusted for cost of living<br>
   v. Yearly bonus adjusted for cost of living<br>
   vi. Allowed weekly telework days<br>
   vii. Leave time<br>
   viii. Number of shares offered<br>
   d. Be offered to perform another comparison or go back to the main menu.
</strong>
<p>
Jobs is already a sorted list, so SortedDisplay will display all of the jobs in the Jobs class. The compareTwo() operation allows the user to pick two jobs and generate the ComparisonTableTwo object, which displays the attributes listed above, pulled from Jobs. The Table display inherits the returnToMain() operation, and has a returnToSortedDisplay() operation as well. 
</p>

<strong>
6. When ranking jobs, a job’s score is computed as the weighted sum of:
AYS + AYB + CSO/4 + (LT _ AYS / 260) - ((260 - 52 _ RWT) \* (AYS / 260) / 8)
where:<br>
AYS = yearly salary adjusted for cost of living<br>
AYB = yearly bonus adjusted for cost of living<br>
CSO = Company shares offered (assuming a 4-year vesting schedule and a price-per-share of $1)<br>
LT = leave time<br>
RWT = telework days per week
</strong>
<p>
New jobs reference the weights stored in Settings to calculate their weightedScore attribute. Jobs used this to sort them as they are added. When weights are edited, Jobs must reweightAndSort() to update all the scores and resort.
</p>

<strong>
7. The user interface must be intuitive and responsive.
</strong>
<p>
The GUI implementation is responsible for this.
</p>

<strong>
8. For simplicity, you may assume there is a single system running the app (no communication or saving between devices is necessary).
</strong>
<p>
No reference to multiple devices has been made.
</p>
