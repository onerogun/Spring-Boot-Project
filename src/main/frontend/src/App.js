import React, { useEffect, useState, useCallback } from "react";
import logo from "./logo.svg";
import "./App.css";
import axios from "axios";
import { useDropzone } from "react-dropzone";

const Products = () => {
  const [products, setProducts] = useState([]);

  const fetchProducts = () => {
    axios
      .get("http://admin.2qn4ziu8xq.us-east-1.elasticbeanstalk.com/getproducts")
      .then(response => {
        console.log(response);
        setProducts(response.data);
      });
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  return products.map((product, index) => {
    return (
      <div className="component" key={index}>
        {product.productId ? (
          <img
            src={`http://admin.2qn4ziu8xq.us-east-1.elasticbeanstalk.com/getproducts/getimage/${product.productId}`}
          />
        ) : null}
        <br />
        <h1>{product.productName}</h1>
        <p>ID: {product.productId}</p>
        <p>Price: ${product.price}</p>
        <MyDropzone {...product} />
        <br />
      </div>
    );
  });
};

function MyDropzone({ productId }) {
  const onDrop = useCallback(acceptedFiles => {
    const file = acceptedFiles[0];

    console.log(file);

    const formData = new FormData();
    formData.append("file", file);
    console.log(productId);

    axios
      .post(
        `http://admin.2qn4ziu8xq.us-east-1.elasticbeanstalk.com/getproducts/saveimage/${productId}`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data"
          }
        }
      )
      .then(() => {
        console.log("file successfully uploaded");
        window.location.reload(true);
      })
      .catch(err => {
        console.log("failed!!!");
        console.log(err);
      });
  }, []);
  const { getRootProps, getInputProps, isDragActive } = useDropzone({ onDrop });

  return (
    <div className="dropZone" {...getRootProps()}>
      <input {...getInputProps()} />
      {isDragActive ? (
        <p>Drop the image here ...</p>
      ) : (
        <p>Drag 'n' drop an image here, or click to select image</p>
      )}
    </div>
  );
}

function App() {
  return (
    <div className="App">
      <Products />
    </div>
  );
}

export default App;
