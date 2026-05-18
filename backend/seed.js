const mongoose = require('mongoose');
const dotenv = require('dotenv');
const User = require('./models/User');
const Cow = require('./models/Cow');
const Expense = require('./models/Expense');
const DiseaseCase = require('./models/DiseaseCase');

dotenv.config();

const seedData = async () => {
  try {
    await mongoose.connect(process.env.MONGO_URI);
    console.log('Connected to DB for seeding...');

    const user = await User.findOne({ email: 'dattatrireddy34@gmail.com' });
    if (!user) {
      console.log('User not found. Please register first.');
      process.exit();
    }

    // Clean existing data for this user
    await Cow.deleteMany({ owner: user._id });
    await Expense.deleteMany({ owner: user._id });
    await DiseaseCase.deleteMany({ owner: user._id });

    // Seed Cows
    const cows = await Cow.create([
      { name: 'Lakshmi', breed: 'Jersey', age: 24, weight: 350, currentYield: 10.5, targetYield: 18, owner: user._id },
      { name: 'Gauri', breed: 'Holstein Friesian', age: 36, weight: 450, currentYield: 19.5, targetYield: 18, owner: user._id },
      { name: 'Suga', breed: 'Jersey', age: 48, weight: 400, currentYield: 10.5, targetYield: 15, owner: user._id },
    ]);

    // Seed Expenses
    await Expense.create([
      { owner: user._id, category: 'Feed & Fodder', itemName: 'Green Fodder (Maize)', amount: 12450, quantity: '150 kg' },
      { owner: user._id, category: 'Medical', itemName: 'Vaccination - FMD', amount: 3250 },
      { owner: user._id, category: 'Labor', itemName: 'Monthly Salary', amount: 5000 },
      { owner: user._id, category: 'Income', itemName: 'Milk Sale - May', amount: 33080 },
    ]);

    // Seed Disease Cases
    await DiseaseCase.create([
      { owner: user._id, cow: cows[0]._id, diseaseName: 'Mastitis', status: 'Active', severity: 'Moderate' },
      { owner: user._id, cow: cows[1]._id, diseaseName: 'Bloat', status: 'Recovered', severity: 'Mild' },
    ]);

    console.log('Seeding complete!');
    process.exit();
  } catch (err) {
    console.error(err);
    process.exit(1);
  }
};

seedData();
