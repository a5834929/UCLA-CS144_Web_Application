=================================================

		B. Spatial Search

=================================================

1. spatialSearch calls basicSearch to find the entries with keywords

2. Among the itemID's from the results of basicSearch, spatialSearch finds the entries within the search region.

3. If the number of the entries found is less than numResultsToReturn, repeat step 1 to 3.

4. If numResultsToReturn is satisfied or the basicSearch results are empty, then stop searching.

5. The length of the final entries is the min(numResultsToReturn, number of entries found).
