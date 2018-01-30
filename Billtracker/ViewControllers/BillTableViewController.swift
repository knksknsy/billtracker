//
//  ViewController.swift
//  Billtracker
//
//  Created by Valdet D. on 22.11.17.
//  Copyright © 2017 Valdet D.. All rights reserved.
//

import UIKit
import Firebase

class BillTableViewController: UIViewController, UITableViewDelegate {

    var firebaseHelper = FirebaseHelper()
    var category: String!
    @IBOutlet weak var billTableView: UITableView!
    
    var billArray = [Bill]()
    
    @IBAction func backtoCat(_ sender: Any) {
        dismiss(animated: true)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        firebaseHelper.getDbBills().child(firebaseHelper.getUserUid()).child(category!).observe(DataEventType.value, with: { (snapshot: DataSnapshot) in
            let enumerator = snapshot.children
            while let rest = enumerator.nextObject() as? DataSnapshot {
                let value = rest.value as! NSDictionary
                print(value)
                let imageId: String = value.value(forKey: "imageId") as! String
                let id: String = value.value(forKey: "id") as! String
                let date: UInt = value.value(forKey: "date") as! UInt
                let sum: Double = value.value(forKey: "sum") as! Double
                let category: String = value.value(forKey: "category") as! String
                let downloadUrl: String = value.value(forKey: "downloadUrl") as! String
                let bill = Bill(category: category, date: date, sum: sum, downloadUrl: downloadUrl, id: id, imageId: imageId)
                self.billArray.append(bill)
                self.billTableView.reloadData()
            }
        })
        billTableView.delegate = self
        billTableView.dataSource = self
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}

extension BillTableViewController: UITableViewDataSource {
    
    func numberOfSectionsInTableView(billTableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ billTableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.billArray.count
    }
    
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            
            //Remove object from array
            billArray.remove(at: indexPath.row)
            
            //Reload tableView
            self.billTableView.reloadData()
        }
    }
    
    func tableView(_ billTableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = billTableView.dequeueReusableCell(withIdentifier: "billCell", for: indexPath) as! BillTableViewCellController
        
        let bill = self.billArray[indexPath.row]
        cell.bill = bill
        cell.bill?.id = bill.id
        cell.billSum?.text = String(bill.sum)
        cell.billDate?.text = bill.printDate()
        let imageUrl:URL = URL(string: bill.downloadUrl!)!
        
        // Start background thread so that image loading does not make app unresponsive
        DispatchQueue.global(qos: .userInitiated).async {
            
            let imageData:NSData = NSData(contentsOf: imageUrl)!
            
            // When from background thread, UI needs to be updated on main_queue
            DispatchQueue.main.async {
                let image = UIImage(data: imageData as Data)
                cell.billImage?.image = image
            }
        }
        
        return cell
    }
}

