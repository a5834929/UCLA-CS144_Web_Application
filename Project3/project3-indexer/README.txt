================================================
        
        A.2: Create Lucene Index

================================================

In order to support the basic keyword search function described before, you will first need to create a Lucene index on a number of attributes of your database tables. Decide which index(es) to create on which attribute(s). Document your design choices and briefly discuss why you've chosen to create your particular index(es) in a plaintext file called README.txt.

    As we are implementing a basic keyword search to have a return of itemIds and names, itemIds and names should be part of the attributes to create on. Moreover, we have to do keyword search on the union of the item name, category, and description attributes. Thus, this union should also be one of attributes. 
    It would be look like:
    
    ----------------------------------------------
    | Index
    | -------------------   ---------------------
    | | Document 1      |   | Document 2        |
    | |                 |   |                   |
    | | Field: ItemId   |   | Field: ItemId     |       ... ...
    | | Field: Name     |   | Field: Name       |
    | | Field: Content  |   | Field: Content    |
    | -------------------   ---------------------
    |
    -----------------------------------------------


