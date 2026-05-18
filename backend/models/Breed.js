const mongoose = require('mongoose');

const BreedSchema = new mongoose.Schema({
  name: {
    type: String,
    required: [true, 'Please add a breed name'],
    unique: true
  },
  image: String,
  description: String,
});

module.exports = mongoose.model('Breed', BreedSchema);
