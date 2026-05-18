const Breed = require('../models/Breed');

exports.getBreeds = async (req, res, next) => {
  try {
    const breeds = await Breed.find();
    res.status(200).json({ success: true, data: breeds });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

exports.seedBreeds = async (req, res, next) => {
  try {
    const initialBreeds = [
      { name: 'Holstein Friesian' },
      { name: 'Jersey' },
      { name: 'Brown Swiss' },
      { name: 'Guernsey' },
      { name: 'Ayrshire' },
      { name: 'Gir' },
      { name: 'Sahiwal' },
    ];
    await Breed.deleteMany();
    const breeds = await Breed.insertMany(initialBreeds);
    res.status(201).json({ success: true, data: breeds });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};
