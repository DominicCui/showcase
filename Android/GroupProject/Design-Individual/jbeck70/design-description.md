## Assignment 5 Design Document

_My responses are below each requirement in italics._

1. When the app is started, the user is presented with the main menu, which allows the user to (1) enter or edit current job details, (2) enter job offers, (3) adjust the comparison settings, or (4) compare job offers (disabled if no job offers were entered yet).  
    * _This is handled by the UI implementation and is not reflected in my design._
    * _The details of database operations (i.e. for job offer persistence across sessions) are not reflected in my design. When calling `createJob()`, the job offer will be stored in a database. Editing a job offer will update it in the database._
1. When choosing to enter current job details, a user will:
    * Be shown a user interface to enter (if it is the first time) or edit all of the details of their current job, which consist of:
        * Title
        * Company
        * Location (entered as city and state)
        * Cost of living in the location (expressed as an index)
        * Yearly salary
        * Yearly bonus
        * Allowed weekly telework days (expressed as the number of days per week allowed for remote work, inclusively between 0 and 5)
        * Leave time (vacation days and holiday and/or sick leave, as a single overall number of days)
        * Number of company shares offered at hiring (valued at $1 per share and expressed as a number >= 0)
        * _This is represented by the `job offer` class, including the `createNewJob()`/`updateJob()` and `makeCurrentJob()` functionality. `makeCurrentJob()` would only be used when creating this instance of a job offer._
    * Be able to either save the job details or cancel and exit without saving, returning in both cases to the main menu.
        * _Saving is handled by the `createNewJob()` and/or `updateJob()` operations (depending on if this is the first time)._
1. When choosing to enter job offers, a user will:
    * Be shown a user interface to enter all of the details of the offer, which are the same ones listed above for the current job.
        * _This is represented by the `job offer` class._
    * Be able to either save the job offer details or cancel.
        * _Saving is handled by the `createNewJob()` and/or `updateJob()` operations._
    * Be able to (1) enter another offer, (2) return to the main menu, or (3) compare the offer (if they saved it) with the current job details (if present).
        * _This is handled by the UI implementation and is not reflected in my design. `createnewJob()` and `compareJobs(job, job)` are available for use._
1. When adjusting the comparison settings, the user can assign integer weights to:
    * Yearly salary
    * Yearly bonus
    * Allowed weekly telework days
    * Leave time
    * Shares offered
        * _This is represented by the `comparison settings` class._

    If no weights are assigned, all factors are considered equal.
    * _Weights default to 1._

1. When choosing to compare job offers, a user will:
    * Be shown a list of job offers, displayed as Title and Company, ranked from best to worst (see below for details), and including the current job (if present), clearly indicated.
        * _`Rank`s are a calculated attribute of the `job offer` class. This attribute is computed from the `comparison settings` class. The `comparison table` class will aggregate all `job offer`s. The current job is the `job offer` with `isCurrentJob` set to true, so we can display is differently._
    * Select two jobs to compare and trigger the comparison.
        * _The `comparison table` class contains the `compareJobs(job, job)` operation._
    * Be shown a table comparing the two jobs, displaying, for each job:
        * Title
        * Company
        * Location
        * Yearly salary adjusted for cost of living
        * Yearly bonus adjusted for cost of living
        * Allowed weekly telework days
        * Leave time
        * Number of shares offered
            * _These are attributes of the `job offer` (the adjusted values are calculated for each `job offer`). The `comparison table` `compareJobs(job, job)` operation and UI code will handle the display functionality._
    * Be offered to perform another comparison or go back to the main menu.
        * _This is handled by the UI implementation and is not reflected in my design._
1. When ranking jobs, a job’s score is computed as the weighted sum of:

AYS + AYB + CSO/4 + (LT * AYS / 260) - ((260 - 52 * RWT) * (AYS / 260) / 8)

where:
AYS = yearly salary adjusted for cost of living
AYB = yearly bonus adjusted for cost of living
CSO = Company shares offered (assuming a 4-year vesting schedule and a price-per-share of $1)
LT = leave time
RWT = telework days per week
The rationale for the RWT subformula is:
value of an employee hour = (AYS / 260) / 8
commute hours per year (assuming a 1-hour/day commute) =
1 * (260 - 52 * RWT)
therefore travel-time cost = (260 - 52 * RWT) * (AYS / 260) / 8

For example, if the weights are 2 for the yearly salary, 2 for the shares offered, and 1 for all other factors, the score would be computed as:


2/7 * AYS + 1/7 * AYB + 2/7 * (CSO/4) + 1/7 * (LT * AYS / 260) - 1/7 * ((260 - 52 * RWT) * (AYS / 260) / 8)

_This is handled by the `rank` calculation (which is a `job offer` calculated attribute and uses the `comparison settings` to provide the weights) and is not reflected in my design._

7. The user interface must be intuitive and responsive.
    * _This is handled by the UI implementation and is not reflected in my design._
1. For simplicity, you may assume there is a single system running the app (no communication or saving between devices is necessary).
    * _This is reflected in my design in that there is no communication or saving between devices mentioned._
