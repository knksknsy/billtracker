//
//  Created by Kaan K. on 29.01.18.
//  Copyright © 2018 Valdet D.. All rights reserved.
//

import Firebase
import FirebaseDatabase
import FirebaseStorage
import FirebaseAuth


class FirebaseHelper {
    
    var auth: Auth!
    var dbUsers: DatabaseReference!
    var dbCategories: DatabaseReference!
    var dbBills: DatabaseReference!
    var imageStorage: StorageReference!
    
    init() {
        auth = Auth.auth()
        dbUsers = Database.database().reference().child("users")
        dbCategories = Database.database().reference().child("users")
        dbBills = Database.database().reference().child("bills")
        imageStorage = Storage.storage().reference()
    }
    
    func getDbCategories() -> DatabaseReference {
        return dbCategories
    }
    
    func getDbBills() -> DatabaseReference {
        return dbBills
    }
    
    func createBill(_ bill: Bill, data: Data) {
        let userUid = getUserUid()
        if (userUid != "") {
            let post:[String : AnyObject] = [
                "id": bill.id as AnyObject,
                "imageId": bill.imageId as AnyObject,
                "category": bill.category as AnyObject,
                "date": bill.date as AnyObject,
                "sum": bill.sum as AnyObject
            ]
            
            // Save category
            dbCategories.child(userUid).child(bill.category).setValue(bill.category)
            
            bill.id = dbBills.childByAutoId().key
            bill.imageId = NSUUID().uuidString
            
            // Save bill information
            dbBills.child(userUid).child(bill.category).child(bill.id!).setValue(post);
            
            // Upload bill image
            uploadImage(bill, data: data);
        }
    }
    
    func deleteBill(_ bill: Bill) {
        let userUid = getUserUid()
        if (userUid != "") {
            // delete bill
            dbBills.child(userUid).child(bill.category).child(bill.id!).removeValue();
            // delete bill image
            imageStorage.child("user").child(userUid).child(bill.imageId!).delete();
        }
    }
    
    func updateBill(_ bill: Bill) {
        let userUid = getUserUid()
        if (userUid != "") {
            let post:[String : AnyObject] = [
                "id": bill.id as AnyObject,
                "imageId": bill.imageId as AnyObject,
                "category": bill.category as AnyObject,
                "date": bill.date as AnyObject,
                "sum": bill.sum as AnyObject,
                "downloadUrl": bill.downloadUrl as AnyObject
            ]
            dbBills.child(userUid).child(bill.category).child(bill.id!).setValue(post);
        }
    }
    
    func deleteCategory(category: String) {
        let userUid = getUserUid()
        if (userUid != "") {
            // delete bill image references
            dbBills.child(userUid).child(category).observe(DataEventType.value, with: { (snapshot: DataSnapshot) in
                let enumerator = snapshot.children
                while let rest = enumerator.nextObject() as? DataSnapshot {
                    let bill: Bill = rest.value as! Bill
                    self.imageStorage.child("user").child(userUid).child(bill.imageId!).delete()
                }
                self.dbBills.child(userUid).child(category).removeValue()
                self.dbCategories.child(userUid).child(category).removeValue()
            })
        }
    }
    
    
    func getUserUid() -> String {
        return (self.auth.currentUser?.uid)!
    }
    
    func uploadImage(_ bill: Bill, data: Data) {
        let userUid = getUserUid()
        if (userUid != "") {
            let metadata = StorageMetadata()
            metadata.contentType = "image/jpeg"
            let uploadTask = imageStorage.child("user").child(userUid).child(bill.imageId!).putData(data, metadata: metadata) { (metadata, error) in
                guard let metadata = metadata else {
                    // Uh-oh, an error occurred!
                    return
                }
                print("downloadURL", metadata.downloadURL()?.absoluteString)
            }
            
            // Listen for state changes, errors, and completion of the upload.
            uploadTask.observe(.resume) { snapshot in
                // Upload resumed, also fires when the upload starts
            }
            
            uploadTask.observe(.pause) { snapshot in
                // Upload paused
            }
            
            uploadTask.observe(.progress) { snapshot in
                // Upload reported progress
                let percentComplete = 100.0 * Double(snapshot.progress!.completedUnitCount)
                    / Double(snapshot.progress!.totalUnitCount)
                print("upload progress", percentComplete)
            }
            
            uploadTask.observe(.success) { snapshot in
                bill.downloadUrl = snapshot.metadata?.downloadURL()?.absoluteString
                print("downloadUrl", bill.downloadUrl)
                self.updateBill(bill)
            }
            
            uploadTask.observe(.failure) { snapshot in
                if let error = snapshot.error as? NSError {
                    switch (StorageErrorCode(rawValue: error.code)!) {
                    case .objectNotFound:
                        // File doesn't exist
                        break
                    case .unauthorized:
                        // User doesn't have permission to access file
                        break
                    case .cancelled:
                        // User canceled the upload
                        break
                        
                        /* ... */
                        
                    case .unknown:
                        // Unknown error occurred, inspect the server response
                        break
                    default:
                        // A separate error occurred. This is a good place to retry the upload.
                        break
                    }
                }
            }
        }
        
    }
}

