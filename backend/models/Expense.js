const mongoose = require('mongoose');

const ExpenseSchema = new mongoose.Schema({
  owner: {
    type: mongoose.Schema.ObjectId,
    ref: 'User',
    required: true,
  },
  cow: {
    type: mongoose.Schema.ObjectId,
    ref: 'Cow',
  },
  category: {
    type: String,
    enum: ['Feed & Fodder', 'Medical', 'Maintenance', 'Labor', 'Other Expenses', 'Income'],
    required: true,
  },
  itemName: {
    type: String,
    required: true,
  },
  amount: {
    type: Number,
    required: true,
  },
  date: {
    type: Date,
    default: Date.now,
  },
  quantity: String,
  unitPrice: Number,
});

module.exports = mongoose.model('Expense', ExpenseSchema);
