const express = require('express');
const { getMe, updateProfile, updatePassword } = require('../controllers/userController');
const { protect } = require('../middleware/authMiddleware');
const { upload } = require('../utils/cloudinary');

const router = express.Router();

router.get('/me', protect, getMe);
router.put('/profile', protect, upload.single('profileImage'), updateProfile);
router.put('/updatepassword', protect, updatePassword);

module.exports = router;
