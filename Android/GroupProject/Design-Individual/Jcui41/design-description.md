# Design Description

The follow are the requirements of job comparison app (bold), and description following the each requirement. Italic and bold world in the description is relative term in the design.

## Requirements

#### 1. When the app is started, the user is presented with the main menu, which allows the user to

​	**1) enter or edit current job details,** 
​	**2) enter job offers,** 
​	**3) adjust the comparison settings,** 
​	**4) compare job offers (disabled if no job offers were entered yet 1).**

​	The above requirements basically is a simple UI route to process the app interact with some button clicks. In the design there is a **route()** method to handle this process. 

#### 2. When choosing to enter current job details, a user will: 

​	**a. Be shown a user interface to enter (if it is the first time) or edit all of the details of their current job, which consist of:** 

​		**i. Title**
​		**ii. Company**
​		**iii. Location (entered as city and state)**
​		**iv. Cost of living in the location (expressed as an index)**
​		**v. Yearly salary**
​		**vi. Yearly bonus**
​		**vii. Allowed weekly telework days (expressed as the number of days per week allowed for remote work, inclusively between 0 and 5)**
​		**viii. Leave time (vacation days and holiday and/or sick leave, as a single overall number of days)**
​		**ix. Number of company shares offered at hiring (valued at $1 per share and expressed as a number >= 0)**

​	All the required input fields are the attributes of ***Job*** class in the design. For money, like *yearly salary* and *yearly bonus* are signed as ***BigDecimal*** type which will provided by the programming language. ***living Cos*t** will expressed as an index, so it be signed to ***int*** type rather than a ***BigDecimal*** . The logic of edit the current job will handle in the ***editCurretnJob*()**,  and sign to the ***curretnJob*** variable. The app would have 0 to many job along the usage of this application. 

​	**b. Be able to either save the job details or cancel and exit without saving, returning in both cases to the main menu.**

​	In the ***JobComparisonApp***, there are ***save()*** and ***cancel()*** methods getting the status of filling job to determining whether save it to the ***JobList*** or not. 

return to the main menu could handled by the UI implementation. 

#### 3. When choosing to enter job offers, a user will:

​	**a. Be shown a user interface to enter all of the details of the offer, which are the same ones listed above for the current job.**

​	As same as 2.a requirement.

​	**b. Be able to either save the job offer details or cancel.**

​	As same as 2.b requirement.	

​	**c. Be able to (1) enter another offer, (2) return to the main menu, or (3) compare the offer (if they saved it) with the current job details (if present).**

​	(1) -> ***JobComparisonApp.addoffer()***	

​	(2) -> handled by the UI implementation

​	(3) -> ***JobComparisonApp.route()*** handle the page switch in the application, ***JobComparisonApp.compareJob()*** will do the job of compare offers.

#### 4. When adjusting the comparison settings, the user can assign integer weights to:

​	**a. Yearly salary**
​	**b. Yearly bonus**
​	**c. Allowed weekly telework days**
​	**d. Leave time**
​	**e. Shares offered**

**If no weights are assigned, all factors are considered equal.**

**ComparisonSetting** class have the above settings with ***int*** type. And there is a constructor to sign those setting to equal if the user doesn't sign any weight.

#### 5.  When choosing to compare job offers, a user will: 

​	**a. Be shown a list of job offers, displayed as Title and Company, ranked from best to worst (see below for details), and including the current job (if present), clearly indicated.**

​	Since the job will show as ranked, ***sortJoblist()*** will do the trick and show it associated with UI. Add a ***currentJob*** variable with ***boolean***

type in ***Job*** class to identify the current job.

​	**b. Select two jobs to compare and trigger the comparison.**

​	***CompareJobs*** class would invoked by  ***JobComparisonApp.compareJobs()***. ***comparible()*** to check whether have enough job (at least two) to compare. ***JobComparisonApp.compareJobs())*** will get the to picked job and call ***CompareJobs.compare(job, job)*** which contains the compare logic. The action of pick two jobs will handled by UI implementation. 

​	**c. Be shown a table comparing the two jobs, displaying, for each job:**

​	***showCompareTable()*** print out the compare result table. 	

​	**d. Be offered to perform another comparison or go back to the main menu.**

​	UI handle the page switching, and a new compare task will relate to  ***JobComparisonApp.compareJobs()***.

#### 6. When ranking jobs, a job’s score is computed as the weighted sum of:

$$
AYS + AYB + CSO/4 + (LT * AYS / 260) - ((260 - 52 * RWT) * (AYS / 260) / 8)
$$

​										**AYS = yearly salary adjusted for cost of living**
​										**AYB = yearly bonus adjusted for cost of living**
​										**CSO = Company shares offered (assuming a 4-year vesting schedule and a price-per-share of $1)**
​										**LT = leave time**
​										**RWT = telework days per week**

​																**The rationale for the RWT subformula is:**
$$
(260 - 52 * RWT) * (AYS / 260) / 8
$$
A ***JobScore*** class created with above five attributes signed with int (since they are all weighted number). All attributes are required parameters of ***score(...)*** which will calculate the score according to the given formula. ***rwtFormula(...)*** will calculate the subequation.  A ***score*** variable added to the class with ***private*** visibility, and also a ***getScore()***.

#### 7. The user interface must be intuitive and responsive.

Related to UI design and implementation.

#### 8. For simplicity, you may assume there is a single system running the app (no communication or saving between devices is necessary).

Singleton application as designed.

. 









