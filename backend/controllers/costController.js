const Expense = require('../models/Expense');
const Cow = require('../models/Cow');

exports.getCostSummary = async (req, res) => {
  try {
    const expenses = await Expense.find({ owner: req.user.id });

    let totalExpense = 0;
    let milkIncome = 0;
    const categoryTotals = {};

    expenses.forEach(exp => {
      if (exp.category === 'Income') {
        milkIncome += exp.amount;
      } else {
        totalExpense += exp.amount;
        categoryTotals[exp.category] = (categoryTotals[exp.category] || 0) + exp.amount;
      }
    });

    const netProfit = milkIncome - totalExpense;
    const cowCount = await Cow.countDocuments({ owner: req.user.id });
    const costPerCow = cowCount > 0 ? totalExpense / cowCount : 0;

    res.status(200).json({
      success: true,
      data: {
        totalExpense,
        milkIncome,
        netProfit,
        costPerCow,
        categoryTotals,
        recentTransactions: expenses.sort((a, b) => b.date - a.date).slice(0, 10)
      }
    });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

exports.addTransaction = async (req, res) => {
  try {
    req.body.owner = req.user.id;
    const transaction = await Expense.create(req.body);
    res.status(201).json({ success: true, data: transaction });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};
