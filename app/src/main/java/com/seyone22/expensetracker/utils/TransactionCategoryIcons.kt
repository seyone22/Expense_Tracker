package com.seyone22.expensetracker.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CreditScore
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

object TransactionCategoryIcons {

    val categoryIcons: Map<String, ImageVector> = mapOf(
        // 🏠 Bills & Utilities
        "Bills" to Icons.AutoMirrored.Filled.ReceiptLong,
        "Telephone" to Icons.Default.Phone,
        "Electricity" to Icons.Default.Bolt,
        "Gas" to Icons.Default.LocalGasStation,
        "Internet" to Icons.Default.Wifi,
        "Rent" to Icons.Default.Home,
        "Cable TV" to Icons.Default.LiveTv,
        "Water" to Icons.Default.WaterDrop,

        // 🍕 Food & Dining
        "Food" to Icons.Default.Fastfood,
        "Groceries" to Icons.Default.ShoppingCart,
        "Dining out" to Icons.Default.LocalDining,

        // 🎭 Leisure & Entertainment
        "Leisure" to Icons.Default.SportsEsports,
        "Movies" to Icons.Default.Movie,
        "Video Rental" to Icons.Default.Videocam,
        "Magazines" to Icons.AutoMirrored.Filled.MenuBook,

        // 🚗 Automobile & Transportation
        "Automobile" to Icons.Default.DirectionsCar,
        "Maintenance" to Icons.Default.Build,
        "Parking" to Icons.Default.LocalParking,
        "Registration" to Icons.Default.Badge,

        // 🎓 Education
        "Education" to Icons.Default.School,
        "Books" to Icons.AutoMirrored.Filled.MenuBook,
        "Tuition" to Icons.Default.AccountBalance,
        "Others" to Icons.Default.QuestionMark,

        // 🏡 Home & Personal Needs
        "Homeneeds" to Icons.Default.Home,
        "Clothing" to Icons.Default.Checkroom,
        "Furnishing" to Icons.Default.Weekend,

        // ❤️ Healthcare & Medical
        "Healthcare" to Icons.Default.MedicalServices,
        "Health" to Icons.Default.Favorite,
        "Dental" to Icons.Default.Face,
        "Eyecare" to Icons.Default.RemoveRedEye,
        "Physician" to Icons.Default.LocalHospital,
        "Prescriptions" to Icons.Default.Medication,

        // 🔒 Insurance
        "Insurance" to Icons.Default.Security,
        "Auto" to Icons.Default.DirectionsCar,
        "Life" to Icons.Default.HeartBroken,
        "Home" to Icons.Default.Home,
        "Health" to Icons.Default.Favorite,

        // 🌍 Travel & Vacation
        "Vacation" to Icons.Default.AirplanemodeActive,
        "Travel" to Icons.Default.Flight,
        "Lodging" to Icons.Default.Hotel,
        "Sightseeing" to Icons.Default.Map,

        // 💰 Taxes
        "Taxes" to Icons.Default.RequestQuote,
        "Income Tax" to Icons.Default.AttachMoney,
        "House Tax" to Icons.Default.Home,
        "Water Tax" to Icons.Default.WaterDrop,

        // 🎁 Miscellaneous & Gifts
        "Miscellaneous" to Icons.Default.Category,
        "Gifts" to Icons.Default.CardGiftcard,

        // 💵 Income & Earnings
        "Income" to Icons.Default.Money,
        "Salary" to Icons.Default.AttachMoney,
        "Reimbursement/Refunds" to Icons.Default.CreditScore,
        "Investment Income" to Icons.AutoMirrored.Filled.TrendingUp,
        "Other Income" to Icons.Default.Payments,

        // ❓ Others
        "Other Expenses" to Icons.Default.MoneyOff,
        "Transfer" to Icons.Default.SwapHoriz,

        // Non category stuff
        "Withdrawal" to Icons.Default.ArrowUpward,
        "Deposit" to Icons.Default.ArrowDownward,
        "Transfer" to Icons.Default.SwapHoriz,
    )

    // Returns an icon for a given category, or a default icon if not found
    fun getIconForCategory(category: String): ImageVector {
        return categoryIcons[category] ?: Icons.AutoMirrored.Filled.HelpOutline
    }
}
