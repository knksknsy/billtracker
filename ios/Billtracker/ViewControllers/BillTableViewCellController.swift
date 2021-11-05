//
//  BillTableViewCellController.swift
//  Billtracker
//
//  Created by Kaan Keskinsoy on 30.01.18.
//  Copyright © 2018 Valdet Dodaj. All rights reserved.
//

import UIKit

class BillTableViewCellController: UITableViewCell {
    
    var firebaseHelper = FirebaseHelper()
    
    var bill: Bill?
    
    @IBOutlet weak var billDate: UILabel!
    @IBOutlet weak var billSum: UILabel!
    @IBOutlet weak var billImage: UIImageView!
    @IBAction func deleteBill(_ sender: Any) {
        print("bill to be deleted", bill?.id)
        firebaseHelper.deleteBill(bill!)
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
