# BillTracker

Billtracker is a APP that can catalog bills. In doing so, invoices are scanned in
first after a purchase. After the invoice has been scanned, the invoice amount must
be entered. In an extended version, the sum of the invoice is automatically recognized
via [tesseract](https://github.com/tesseract-ocr/tesseract)
([tess-two](https://github.com/rmtheis/tess-two)), an OCR. After the invoice amount
has been entered, you will be asked to assign the invoice to a category and to
enter a valid corresponding name for the invoice. In the following step, the bill
must be saved, this happens by pressing the appropriate button. With the click on
save, the image is encoded with bytestream and uploaded to Firebase. There we have
created a cloud-hosted database, so if a picture is missing this will be
automatically synced from network storage to device storage. Actually if you want
to view all or individual invoices in the categories, you can switch to the
corresponding window. Here you will find an overview of all invoices. Click on a
special category and continue with the click on a special invoice to get to the
detail view. In this detail view, the defined amount or further details can
optionally be edited again. Also the deletion of individual invoices or entire
categories was considered. In doing so, app data is either paged on Firebase or
deleted. For this we have created a testaccount, which is also required for login:

```Credentials
USR: ***REMOVED***
PSW: ***REMOVED***
```

## Requirements
 * Media and Camera
    - in Android we use [camera2](https://developer.android.com/reference/android/hardware/camera2/package-summary.html)
 * Data Storage & Networking
    - here we use [Firebase](https://console.firebase.google.com)

## Testing

* Android

  For the test we have created an instrumented test case. To run this test  under android go to `app/src/andoridTest` and press right mousekey on `java` and select `Run 'All Test'`. This will test:

   * take and save a bill
   * check if bill exist
   * edit bill
   * delete bill


* iOS

  * *WIP*

## Team

* Artur B.
* Valdet D.
* Kaan K.

## License

[MIT License](LICENSE.md)
