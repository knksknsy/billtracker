//
//  BillTableViewCellController.swift
//  Billtracker
//
//  Created by Kaan K. on 30.01.18.
//  Copyright © 2018 Valdet D.. All rights reserved.
//

import UIKit

class BillTableViewCellController: UITableViewCell {
    
    @IBOutlet weak var billDate: UILabel!
    @IBOutlet weak var billSum: UILabel!
    @IBOutlet weak var billImage: UIImageView!

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
