const functions = require("firebase-functions");
const cors = require("cors")({ origin: true });

const { GoogleGenerativeAI } = require("@google/generative-ai");

const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);

exports.chat = functions.https.onRequest((req, res) => {

  cors(req, res, async () => {

    try {

      const message = req.body.message;

      const model = genAI.getGenerativeModel({
        model: "gemini-1.5-flash",
      });

      const result = await model.generateContent(message);

      const response = await result.response;

      const text = response.text();

      res.json({
        reply: text
      });

    } catch (error) {

      res.status(500).json({
        error: error.message
      });
    }
  });
});