//
//  ViewController.swift
//  Billtracker
//
//  Created by Valdet Dodaj on 22.11.17.
//  Copyright © 2017 Valdet Dodaj. All rights reserved.
//

import UIKit
import Firebase

class ContentViewController: UIViewController, UITableViewDelegate {
//    @IBAction func openBill(_ sender: Any) {
//        performSegue(withIdentifier: "categoryParameter", sender: self)
//    }
    
    @IBOutlet weak var tableView: UITableView!
    
    var firebaseHelper = FirebaseHelper()
    
    var categoryArray = [String]()
    
    var valueToPass:String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        firebaseHelper.getDbCategories().child(firebaseHelper.getUserUid()).observe(DataEventType.value, with: { (snapshot: DataSnapshot) in
            let enumerator = snapshot.children
            while let rest = enumerator.nextObject() as? DataSnapshot {
                let category: String = rest.value as! String
                self.categoryArray.append(category)
                self.tableView.reloadData()
            }
        })
        tableView.delegate = self
        tableView.dataSource = self
        self.tableView.reloadData()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}

extension ContentViewController: UITableViewDataSource {
    
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.categoryArray.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "categoryCell", for: indexPath) as! CategoryTableViewCell
        
        let categoryName = self.categoryArray[indexPath.row]
        cell.categoryLabel?.text = categoryName
        valueToPass = self.categoryArray[indexPath.row]
        return cell
    }
    
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            
            //Remove object from array
            categoryArray.remove(at: indexPath.row)
            
            //Reload tableView
            self.tableView.reloadData()
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        performSegue(withIdentifier: "categoryParameter", sender: self)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if (segue.identifier == "categoryParameter") {
            
            // initialize new view controller and cast it as your view controller
            var viewController = segue.destination as! BillTableViewController
            // your new view controller should have property that will store passed value
            viewController.category = valueToPass
        }
        
    }

}
