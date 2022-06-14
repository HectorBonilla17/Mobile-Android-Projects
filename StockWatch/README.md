Using IEX's stock API, the app will retrieve information on each stock in the user's list, ex: price & the amount that has changed today. The app allows for user-input to attempt to add a stock, which it will do so if the stock symbol is recognized the API otherwise it will inform the user that stock symbol is invalid.

![Stock Main](https://user-images.githubusercontent.com/31080342/173478711-54fc11a6-a8a5-4bcf-81da-c182dd12e2b6.PNG)


If the stock symbol is recognized by the API it will import the data into the application, saving it the app's json file, and update the user's list to have the stock displayed.
![Stock Add](https://user-images.githubusercontent.com/31080342/173478716-43095fee-fcfe-4229-9560-8d296b141016.PNG)


If the stock symbol is not recognized by the API it will throw an alert stating that data for the stock could not be found and no stock will be added to the user's list.
![Stock Fail](https://user-images.githubusercontent.com/31080342/173478724-085f4a92-3772-4763-9aae-37ffb41d5442.PNG)
