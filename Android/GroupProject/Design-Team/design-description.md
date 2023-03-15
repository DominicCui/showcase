# Design Description

The following are the requirements of the job comparison app (bold), and description following each requirement. The Italic and bold word in the description is a relative term in the design.

## Requirements
#### 1. When the app is started, the user is presented with the main menu, which allows the user to

1. **enter or edit current job details,** 
1. **enter job offers,**
1. **adjust the comparison settings** 
1. **compare job offers (disabled if no job offers were entered yet 1).**
   The above requirements basically is a simple UI route to process the app interact with some button clicks. In the design there is a _**route()**_ method to handle those UI processes. _**CompareJobs.comparable()**_ to check whether there are enough jobs (at least two) in the list to compare. 

#### 2. When choosing to enter current job details, a user will: 

* **a. Be shown a user interface to enter (if it is the first time) or edit all of the details of their current job, which consist of:** 
    - **i. Title** 
    - **ii. Company** 
    - **iii. Location (entered as city and state)** 
    - **iv. Cost of living in the location (expressed as an index)** 
    - **v. Yearly salary** 
    - **vi. Yearly bonus** 
    - **vii. Allowed weekly telework days (expressed as the number of days per week allowed for remote work, inclusively between 0 and 5)** 
    - **viii. Leave time (vacation days and holiday and/or sick leave, as a single overall number of days)** 
    - **ix. Number of company shares offered at hiring (valued at $1 per share and expressed as a number >= 0)**	
	
    All the required input fields are the attributes of _**Job**_ class in the design. For money, like *yearly salary* and *yearly bonus* are signed as _**double**_ type.	
    _**living Cost**_ will expressed as an index, so it be signed to _**int**_ type rather than a _**double**_ .  
    The logic of edit the current job will handle in the _**editCurrentJob()**_ , and return to the _**currentJob**_ .  
    The app would have 0 to many jobs along the usage of this application. 
* **b. Be able to either save the job details or cancel and exit without saving, returning in both cases to the main menu.**

	In the _**JobComparisonApp**_, there are _**save()**_ and _**cancel()**_ methods getting the status to determine whether save it to the _**JobList**_ or not. 	Return to the main menu could be handled by the UI implementation. 

#### 3. When choosing to enter job offers, a user will:

* **a. Be shown a user interface to enter all of the details of the offer, which are the same ones listed above for the current job.**	
  
	As same as 2.a requirement. The difference is that the logic of job addition will handled in the _**addOffer()**_ , and save in the _**jobList**_ .
  
* **b. Be able to either save the job offer details or cancel.**
	As same as 2.b requirement.	
      
	* **c. Be able to (1) enter another offer, (2) return to the main menu, or (3) compare the offer (if they saved it) with the current job details (if present).**
  
	(1) -> _**JobComparisonApp.addOffer()**_   
    (2) -> handled by the UI implementation   
    (3) -> _**JobComparisonApp.route()**_ handle the page switch in the application (from main menu to comparison page), _**JobComparisonApp.compareJob()**_ will compare the saved job with the current job, if not set the current job then throw an error.

#### 4. When adjusting the comparison settings, the user can assign integer weights to:

- **a. Yearly salary**
- **b. Yearly bonus**
- **c. Allowed weekly telework days**
- **d. Leave time**
- **e. Shares offered** 
**If no weights are assigned, all factors are considered equal.**

_**ComparisonSetting**_ class have the above settings with _**int**_ type. 
There is a constructor to sign those settings a default value to make them equal if the user doesn't customize any weights.

#### 5. When choosing to compare job offers, a user will: 

- **a. Be shown a list of job offers, displayed as Title and Company, ranked from best to worst (see below for details), and including the current job (if present), clearly indicated.**

	Since the job will show as ranked, _**sortJoblist()**_ will do the trick return to _**rankedList**_ and show it associated with the UI.  
    Add a _**currentJob**_ variable with _**boolean**_ type in _**Job**_ class to identify the current job.

- **b. Select two jobs to compare and trigger the comparison.**
  
    _**CompareJobs**_ class would invoked by _**JobComparisonApp.compareJobs()**_. 
    _**CompareJobs.comparible()**_ to check whether there are enough jobs (at least two) in the list to compare. 
    _**JobComparisonApp.compareJobs()**_ will get the two picked jobs and call _**CompareJobs.compare(job, job)**_ which contains the compare logic. 
    The action of pick two jobs will be handled by UI implementation. 

- **c. Be shown a table comparing the two jobs, displaying, for each job:**

    _**CompareJobs.showCompareTable()**_ prints out the compare result table with details. 	

- **d. Be offered to perform another comparison or go back to the main menu.**
      
      UI would provide an option of new comparison, and a new compare task will relate to _**JobComparisonApp.compareJobs()**_.
      
#### 6. When ranking jobs, a job’s score is computed as the weighted sum of:

```mathematica
AYS + AYB + CSO/4 + (LT * AYS / 260) - ((260 - 52 * RWT) * (AYS / 260) / 8)
```
- **AYS = yearly salary adjusted for cost of living**
- **AYB = yearly bonus adjusted for cost of living**
- **CSO = Company shares offered (assuming a 4-year vesting schedule and a price-per-share of $1)**
- **LT = leave time**
- **RWT = telework days per week**
- **The rationale for the RWT subformula is:**
```mathematica
(260 - 52 * RWT) * (AYS / 260) / 8 
```

Except the _**AYS**_ and _**AYB**_, the other elements would as same as the attributes in _**Job**_ which talked above at 2.a.
_**AYS**_ and _**AYB**_ signed to double.
_**Job.adjustIncome(...)**_ will apply the living cost to the _**yearlySalary**_ and _**yearlyBonus**_.
All attributes in this section are required parameters for _**Job.calculateScore(...)**_ which will calculate the score according to the given formula. _**rwtFormula(...)**_ will calculate the sub-equation. 
A ***score*** variable added to the class. The precision is not an essential requirement, that is why it is signed as int.

#### 7. The user interface must be intuitive and responsive.

Related to UI design and implementation.

#### 8. For simplicity, you may assume there is a single system running the app (no communication or saving between devices is necessary).

Singleton application as designed.
