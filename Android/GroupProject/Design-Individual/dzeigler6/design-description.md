Dylan Zeigler  
dzeigler6  
CS-6300-SDP  
Assignment 5  
Design Description  
  

```
1. When the app is started, the user is presented with the main menu, which allows the user to (1) enter or edit current job details, (2) enter job offers, (3) adjust the comparison settings, or (4) compare job offers (disabled if no job offers were entered yet).  
```

This requirement is met by the MainMenu class which gives a user 4 functions. The updateCurrentJob function allows a user to (1) enter or edit current job details. The enterJobOffer function allows a user to (2) enter job offers. The adjustComparisonSettings function allows a user to (3) adjust the comparison settings. Finally, the compareJobs function allows a user to (4) compare job offers. In the MainMenu class the current job and the job offers are stored in data structures which allows the MainMenu class to disable the compare job offers feature if no job offers are present. 

```
2. When choosing to enter current job details, a user will:  
    a. Be shown a user interface to enter (if it is the first time) or edit all of the details of their current job, which consist of:  
        i. Title  
        ii. Company  
        iii. Location (entered as city and state)  
        iv. Cost of living in the location (expressed as an index)  
        v. Yearly salary  
        vi. Yearly bonus  
        vii. Allowed weekly telework days (expressed as the number of days per week allowed for remote work, inclusively between 0 and 5)  
        viii. Leave time (vacation days and holiday and/or sick leave, as a single overall number of days)  
        ix. Number of company shares offered at hiring (valued at $1 per share and expressed as a number >= 0)  
    b. Be able to either save the job details or cancel and exit without saving, returning in both cases to the main menu.  
```

After the user selects the updateCurrentJob function from the main menu, a new class called CurrentJob will be created. This class is a child of the Job class which holds all the information i-ix. The CurrentJob class will give users a method to update any of the attributes in the Job class through the updateAttribute function. Also, since the updateCurrentJob function passes in the CurrentJob object from the MainMenu class, either the CurrentJob attributes will be loaded into the UI for edit, or if the CurrentJob is null, then new current job information will be requested to help satisfy the requirement in 2.a. The CurrentJob class has two special methods for saving and canceling the CurrentJob information to satisfy the requirement in 2.b. Finally, if the save method is invoked, the currentJob object in the MainMenu class will be updated to hold the most up to date current job.

```
3. When choosing to enter job offers, a user will:  
    a. Be shown a user interface to enter all of the details of the offer, which are the same ones listed above for the current job.  
    b. Be able to either save the job offer details or cancel.  
    c. Be able to (1) enter another offer, (2) return to the main menu, or (3) compare the offer (if they saved it) with the current job details (if present).  
```

After the user selects the enterJobOffer function the app creates a JobOffer class that is a child of the same Job class mentioned above. This means that it offers the same information as the current job to satisfy requirement 3.a. The JobOffer class offers its own save and cancel methods to handle the cleanup of the job offer object. If the user saves the job offer, it will update the jobOfferList in the MainMenu class to hold to most up to date job offers. Finally, separate methods are given in the JobOffer class to enter another offer, which will create a new JobOffer object, return to the main menu and compare the offer to the current job. In order to keep track of whether the offer was saved or not, the JobOffer class contains a saved boolean to let the user know if the current offer is saved or not. This way if a user executes a comparison in requirement 3.c.3, the current saved job offer and the current job can be passed along to a CompareJobs class which will handle comparing the attributes of the jobs. 


```
4. When adjusting the comparison settings, the user can assign integer weights to:  
    a. Yearly salary  
    b. Yearly bonus  
    c. Allowed weekly telework days  
    d. Leave time  
    e. Shares offered  
If no weights are assigned, all factors are considered equal.  
```

When a user selects the adjustComparisonSettings function from the MainMenu class, the ComparisonSettings class will hold all the weights in requirements 4.a - 4.e. These weights can be updated and retrieved with the ComparisonSettings class and will be initialized to 1 to make all factors equal when no weights are set. 


```
5. When choosing to compare job offers, a user will:  
    a. Be shown a list of job offers, displayed as Title and Company, ranked from best to worst (see below for details), and including the current job (if present), clearly indicated.  
    b. Select two jobs to compare and trigger the comparison.  
    c. Be shown a table comparing the two jobs, displaying, for each job:  
        i. Title  
        ii. Company  
        iii. Location  
        iv. Yearly salary adjusted for cost of living  
        v. Yearly bonus adjusted for cost of living  
        vi. Allowed weekly telework days  
        vii. Leave time  
        viii. Number of shares offered  
    d. Be offered to perform another comparison or go back to the main menu.  
```

When the user selects the compareJobs function from the MainMenu class, 2 classes will be created to help fullfill requirement 5. The first class is the Comparison class which will act as an orchstrator for comparisons and rankings. Since requirement 5.a states that the ranking must be shown right away (without explicit user input after migrating from the MainMenu), an event would cause the computeJobRanks to execute to display the rankings of jobOffers with the currentJob included. The computeJobRanks function will create a JobRankList class that is in charge of generating the ranked list. The currentJob and list of jobOffers will be passed down from the MainMenu class to ensure the most update to date information is displayed. Since these are Job objects, the Title and Company information will be present and the current job can be indicated which satisfies requirement 5.a. Requirement 5.b allows a user to select 2 jobs in the ranking list, therefore the Comparison class has 2 class objects that will hold the currently selected jobs in the ranked list and will pass those jobs to the CompareJobs class for comparison like in requirement 3.c.3. Since the Job class can compute items 5.c.iv and 5.c.v to adjust for cost of living, the Comparison class will be able to display these attributes. Since attributes in requirements 5.c.i - 5.c.viii can be satisfied through the Job objects being displayed in the CompareJobs class, these requrements are fullfilled. Finally, the compareSelectedJobs is asynchronous for user input and a returnToMainMenu function is offered, requirement 5.d is satisfied.


```
6. When ranking jobs, a job’s score is computed as the weighted sum of:  
  
    AYS + AYB + CSO/4 + (LT * AYS / 260) - ((260 - 52 * RWT) * (AYS / 260) / 8)  
  
    where:  
    AYS = yearly salary adjusted for cost of living  
    AYB = yearly bonus adjusted for cost of living  
    CSO = Company shares offered (assuming a 4-year vesting schedule and a price-per-share of $1)  
    LT = leave time  
    RWT = telework days per week  
    The rationale for the RWT subformula is:  
        a. value of an employee hour = (AYS / 260) / 8  
        b. commute hours per year (assuming a 1-hour/day commute) =
            1 * (260 - 52 * RWT)  
        c. therefore travel-time cost = (260 - 52 * RWT) * (AYS / 260) / 8  
  
    For example, if the weights are 2 for the yearly salary, 2 for the shares offered, and 1 for all other factors, the score would be computed as:  
  
  
    2/7 * AYS + 1/7 * AYB + 2/7 * (CSO/4) + 1/7 * (LT * AYS / 260) - 1/7 * ((260 - 52 * RWT) * (AYS / 260) / 8)  
```

Requirement 6 was not explicitly shown in my design as the details to job rankings will be handled entirely within the JobRankList class. The only dependency shown is the connected between the JobRankList class and the ComparisonSettings class were the JobRankList class will retrieve current weights from the ComparisonSettings class for its calcualtions. 


```
7. The user interface must be intuitive and responsive.
```

Requirement 7 is not explicitly shown but the intuitive nature and the responsive behavior of the app will be handled by the GUI implementation.

```
8. For simplicity, you may assume there is a single system running the app (no communication or saving between devices is necessary).
```

Requirement 8 is not explicitly shown as the class relationships do not depend on external network drivers for cross device communication. Also, since the attributes about jobs are saved as objects, there is no needed to talk to database drivers for persistant storage.